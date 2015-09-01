
#include "WProgram.h"

#include "rraptor_config.h"
#include "rraptor_protocol.h"

#include "stepper.h"


extern stepper *_sm_x, *_sm_y, *_sm_z;

/**
 * Получить шаговый двигатель по уникальному имени.
 *
 * @param id - имя мотора, состоит из одной буквы, регистр не учитывается.
 */
static stepper* stepper_by_id(char id) {
    if(id == 'x' || id == 'X') {
        return _sm_x;
    } else if(id == 'y' || id == 'Y') {
        return _sm_y;
    } else if(id == 'z' || id == 'Z') {
        return _sm_z;
    } else {
        return NULL;
    }
}



//static int prevTime1 = 0;

/** 
 * Команда G-code G0 - прямая линия.
 * 
 * @param motor_names имена моторов.
 * @param cvalues значения координат.
 * @param pcount количество параметров (моторов в списке).
 */
int cmd_gcode_g0(char motor_names[], double cvalues[], int  pcount, char* reply_buffer) {
    #ifdef DEBUG_SERIAL
        Serial.print("cmd_gcode_g01: ");
    
        for(int i = 0; i < pcount; i++) {
            Serial.print(motor_names[i]);
            Serial.print("=");
            Serial.print(cvalues[i], DEC);
            Serial.print("");
        }
        Serial.println();
    #endif // DEBUG_SERIAL
        
    if(is_cycle_running()) {
        // устройство занято
        strcpy(reply_buffer, REPLY_BUSY);
    } else {
        // Подготовить шаги для моторов
        bool prepared = false;
        
        if(pcount == 1) {
            // один мотор - линия по одной координате
            // с максимальной скоростью
            stepper *sm = stepper_by_id(motor_names[0]);
            if(sm != NULL) {
                prepare_line(sm, cvalues[0], 0);
                prepared = true;
            }
        } else if(pcount >= 2) {
            // два мотора - линия по плоскости в 2х координатах
            // с максимальной скоростью
            stepper *sm1 = stepper_by_id(motor_names[0]);
            stepper *sm2 = stepper_by_id(motor_names[1]);
            if(sm1 != NULL && sm2 != NULL) {
                prepare_line_2d(sm1, sm2, cvalues[0], cvalues[1], 0);
                prepared = true;
            }
        }
        
        if(prepared) {
            // запустить шаги
            start_stepper_cycle();
            
            // команда выполнена
            strcpy(reply_buffer, REPLY_OK);
                    
            // Заблокируем на время рисования (TODO: убрать)
//            int currTime = millis();
//            while(is_cycle_running()) {
//                if( (currTime - prevTime1) >= 1000) {
//                    prevTime1 = currTime;
//                    Serial.print("X.pos=");
//                    Serial.print(_sm_x->current_pos, DEC);
//                    Serial.print(", Y.pos=");
//                    Serial.print(_sm_y->current_pos, DEC);
//                    Serial.print(", Z.pos=");
//                    Serial.print(_sm_z->current_pos, DEC);
//                    Serial.println();
//                }
//            }
        } else {
            // ошибка - не нашли нужные моторы
            strcpy(reply_buffer, REPLY_ERROR);
        }
    }
    return strlen(reply_buffer);
}

/** 
 * Команда G-code G01 - прямая линия.
 * 
 * @param motor_names имена моторов.
 * @param cvalues значения координат.
 * @param pcount количество параметров (моторов в списке).
 * @param f скорость перемещения мм/с.
 */
int cmd_gcode_g01(char motor_names[], double cvalues[], int  pcount, double f, char* reply_buffer) {
    #ifdef DEBUG_SERIAL
        Serial.print("cmd_gcode_g01: ");
    
        for(int i = 0; i < pcount; i++) {
            Serial.print(motor_names[i]);
            Serial.print("=");
            Serial.print(cvalues[i], DEC);
            Serial.print(", ");
        }
        Serial.print("F=");
        Serial.print(f, DEC);
        Serial.println();
    #endif // DEBUG_SERIAL
        
    if(is_cycle_running()) {
        // устройство занято
        strcpy(reply_buffer, REPLY_BUSY);
    } else {
        bool prepared = false;
        
        if(pcount == 1) {
            stepper *sm = stepper_by_id(motor_names[0]);
            if(sm != NULL) {
                prepare_line(sm, cvalues[0], f);
                prepared = true;
            }
        } else if(pcount >= 2) {
            stepper *sm1 = stepper_by_id(motor_names[0]);
            stepper *sm2 = stepper_by_id(motor_names[1]);
            if(sm1 != NULL && sm2 != NULL) {
                prepare_line_2d(sm1, sm2, cvalues[0], cvalues[1], f);
                prepared = true;
            }
        }
        
        if(prepared) {
            // запустить шаги
            start_stepper_cycle();
            
            // команда выполнена
            strcpy(reply_buffer, REPLY_OK);
                    
            // Заблокируем на время рисования (TODO: убрать)
//            int currTime = millis();
//            while(is_cycle_running()) {
//                if( (currTime - prevTime1) >= 1000) {
//                    prevTime1 = currTime;
//                    Serial.print("X.pos=");
//                    Serial.print(_sm_x->current_pos, DEC);
//                    Serial.print(", Y.pos=");
//                    Serial.print(_sm_y->current_pos, DEC);
//                    Serial.print(", Z.pos=");
//                    Serial.print(_sm_z->current_pos, DEC);
//                    Serial.println();
//                }
//            }
        } else {
            // ошибка - не нашли нужные моторы
            strcpy(reply_buffer, REPLY_ERROR);
        }
    }
    return strlen(reply_buffer);
}

/** 
 * Команда G-code G02 - дуга по часовой стрелке.
 */
void cmd_gcode_g02() {
    #ifdef DEBUG_SERIAL
        Serial.println("cmd_gcode_g02");
    #endif // DEBUG_SERIAL
}

/** 
 * Команда G-code G03 - дуга против часовой стрелки.
 */
void cmd_gcode_g03() {
    #ifdef DEBUG_SERIAL
        Serial.println("cmd_gcode_g03");
    #endif // DEBUG_SERIAL
}
