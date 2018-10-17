#!/bin/sh
source /etc/profile

cd /db/Learns/rocksdb/
if [ ! -d logs ];then
   mkdir logs;
fi
nohup gradle RocksDBTest >/db/Learns/rocksdb/logs/RocksDBTest41.out 2>&1 &
