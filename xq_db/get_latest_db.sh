vulnerabilities_url="https://downloads.internal.xq/latest.sql.gz"

if ping -c 1 "downloads.internal.xq" &> /dev/null
then
	curl "$vulnerabilities_url" -z latest.sql.gz -o latest.sql.gz
	gunzip -kf latest.sql.gz  
else
  echo 0
fi
