#!/bin/bash
#docker-entrypoint.sh
mysql_install_db --user=mysql
mysqld --user=mysql &
sleep 5
mysql -u root -e "CREATE DATABASE xq_vulns"
echo here again
mysql -u root -D xq_vulns < /usr/sql/latest.sql
