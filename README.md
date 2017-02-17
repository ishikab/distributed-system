# distributed-system labs

currently can run the program with:

`mvn clean compile exec:java "-Dexec.args=CONFIG_FILE NODE_NAME CLOCK_TYPE"`

for example, run a china program with vector timestamps and config file lab0.yaml should be:

`mvn clean compile exec:java "-Dexec.args=lab0.yaml china vector"`