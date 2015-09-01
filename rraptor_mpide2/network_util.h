#ifndef NETWORK_UTIL_H
#define NETWORK_UTIL_H

#include <DNETcK.h>
#include <DWIFIcK.h>

/**
 * Конвертировать строковое представление адреса IPv4
 * в объект DNETcK::IPv4
 * @param ipv4_str срока с адресом ipv4 в формате "192.168.1.27".
 * @return адрес DNETcK::IPv4; DNETcK::zIPv4 (IPv4-адрес, заполенный нулями) 
 *     в случае ошибки парсинга строки.
 */
IPv4 parseIPAddress(char* ipv4_str);

/**
 * Получить строковое представление адреса DNETcK::IPv4
 */
void printIPAddress(IPv4 *ipAddress);

/**
 * Вывести текущий статус сети.
 */
void printNetworkStatus();

/**
 * Вывести статус клиента TCP.
 */
void printTcpClientStatus(TcpClient *tcpClient);

/**
 * Вывести название статуса DNETcK::STATUS в виде строки.
 */
void printDNETcKStatus(DNETcK::STATUS status);

#endif // NETWORK_UTIL_H

