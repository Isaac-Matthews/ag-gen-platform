FROM mariadb:latest

ENV MYSQL_ROOT_PASSWORD root
ENV MYSQL_DATABASE xq_vulns
#RUN mkdir /usr/sql
#RUN chmod 644 /usr/sql
#RUN chown -R mysql:mysql /var/lib/mysql
ADD latest.sql /docker-entrypoint-initdb.d/latest.sql
RUN chown -R mysql:mysql /docker-entrypoint-initdb.d/latest.sql
#ADD xq_db.sh /usr/sql/xq_db.sh
#RUN chmod 755 /usr/sql/xq_db.sh
#RUN /usr/sql/xq_db.sh

#EXPOSE 3306
#CMD ["mysqld"]
