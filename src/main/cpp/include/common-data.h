//
// Created by Fear1ess on 2020/11/5.
//

#ifndef DEMO_COMMON_DATA_H
#define DEMO_COMMON_DATA_H

#define INDEX_PROCSELFSTATUS 0

#define MAX_SIZE 20

struct sysinfo {
    long uptime;             /* Seconds since boot */
    unsigned long loads[3];  /* 1, 5, and 15 minute load averages */
    unsigned long totalram;  /* Total usable main memory size */
    unsigned long freeram;   /* Available memory size */
    unsigned long sharedram; /* Amount of shared memory */
    unsigned long bufferram; /* Memory used by buffers */
    unsigned long totalswap; /* Total swap space size */
    unsigned long freeswap;  /* Swap space still available */
    unsigned short procs;    /* Number of current processes */
    unsigned long totalhigh; /* Total high memory size */
    unsigned long freehigh;  /* Available high memory size */
    unsigned int mem_unit;   /* Memory unit size in bytes */
    char _f[20-2*sizeof(long)-sizeof(int)];
    /* Padding to 64 bytes */
};

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
