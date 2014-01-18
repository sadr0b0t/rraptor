
#ifndef _step42_motor_lib_
#define _step42_motor_lib_

typedef struct motor_info {
    /* Motor name */
    char* name;
    
    /* Movement distance per movement cycle, micrometre */
    int distance_per_cycle;
    
    /* Time spent for movement cycle, millis */
    int time_per_cycle;
} motor_info;

typedef struct motor_conn_4pin {
    int MOTOR_PIN1;
    int MOTOR_PIN2;
    int MOTOR_PIN3;
    int MOTOR_PIN4;

    /* Delay between 2 steps */
    int step_delay;

    /* true (1): motor in microstem mode, 
     * false (0): motor in non-microstep mode */
    int microstep;
    
} motor_conn_4pin;

typedef struct step_data {
    motor_info* minfo;
    motor_conn_4pin* mconn;
    int dl;
    int dt;
} step_data;

#endif

