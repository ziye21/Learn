#!/bin/sh
source /etc/profile

cd /db/Learns/rocksdb/
if [ ! -d logs ];then
   mkdir logs;
fi
nohup gradle PalDBTest >/db/Learns/rocksdb/logs/PalDBTest41.out 2>&1 &
