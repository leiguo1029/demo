//
// Created by Fear1ess on 2020/11/5.
//

#ifndef DEMO_COMMON_DATA_H
#define DEMO_COMMON_DATA_H

#include <sys/sysinfo.h>
#include <sys/statfs.h>
#include <sys/utsname.h>
#include <sys/socket.h>
#include <linux/netlink.h>

#define INDEX_PROCSELFSTATUS 0

#define MAX_SIZE 20


struct ctx {
    void *load_addr;
    void *dynstr;
    void *dynsym;
    void *symtab;
    void* strtab;
    int nsymtabs;
    int nsyms;
    off_t bias;
};

#endif //DEMO_COMMON_DATA_H
