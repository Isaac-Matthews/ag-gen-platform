FROM isaacmatthews/mulval:latest
MAINTAINER Isaac Matthews <I.J.Matthews2@ncl.ac.uk>

ENV XSBHOME='~/XSB/'
WORKDIR /root
RUN echo "jdbc:mysql://localhost:3306/nvd\nroot\nroot" > \
  config.txt
RUN echo "#!/usr/bin/env bash" > \
  createDatabase.bash && \
  echo 'service mysql restart\nsleep 5\nmysql -uroot -proot -e "create database nvd"' >> \
  createDatabase.bash && \
  echo "nvd_sync.sh\nexit 0" >> \
  createDatabase.bash && \
  chmod +x createDatabase.bash && \
  ./createDatabase.bash
RUN rm -rf nvd_xml_files
RUN echo "#!/usr/bin/env bash" > \
  startSql.bash && \
  echo "service mysql start\nexit 0" >> \
  startSql.bash && \
  chmod +x startSql.bash
ENV SCPT='./mulval/utils/nessus_translate.sh'
RUN echo $SCPT
RUN tail -n +2 "$SCPT" > "$SCPT.tmp" && mv "$SCPT.tmp" "$SCPT"
RUN sed -i '1s/^/#!\/bin\/sh\ncp ~\/config.txt .\/config.txt\n/' "$SCPT"
RUN chmod 775 $SCPT
ADD scripts.tar.gz /root/
RUN chmod 775 /root/scripts/openvas_translate.py
RUN chmod 775 /root/scripts/process_dot.py
ENV PATH /root/scripts:${PATH}
RUN python /root/scripts/get-pip.py
RUN pip install mysql-connector-python mysql-connector-python
RUN pip install pydot
RUN pip install graphviz
RUN chmod 775 /root/scripts/compose.sh
RUN chmod 775 /root/scripts/compose-generate.sh
WORKDIR /input
ENTRYPOINT /root/createDatabase.bash && /root/startSql.bash && compose.sh
