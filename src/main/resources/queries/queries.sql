
# update creation_date with a random value
update link set creation_date = creation_date +
       random() * (timestamp '2014-01-20 20:00:00' -
                   timestamp '2014-01-10 10:00:00');