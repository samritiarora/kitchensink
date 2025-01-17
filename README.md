# Kitchen Sink
- migrated from

## Key Considerations
* data migration

## TODO
### Phase 1
* api
    * POST /v1/members
    * GET /v1/members
    * GET /v1/members/{memberId}
* change db to mongo
* log
* exception handling
* validations
* security
    * role
        * admin
            * get all
            * add member
        * user
            * get only self
* introduce pagination 
* UI
* test cases using copilot
* documentation

### Phase 2:
* coverage
* deployment
    * docker
* swagger
* flyway or liquibase
* add more endpoints
* cloud
* minikube

## Tools

### Java Switch
/usr/libexec/java_home -V  
sudo ln -sfn /usr/local/opt/openjdk@11/libexec/openjdk.jdk /Library/Java/JavaVirtualMachines/openjdk-11.jdk
export JAVA_HOME=/usr/local/Cellar/openjdk@11/11.0.25/libexec/openjdk.jdk/Contents/Home

sudo ln -sfn $HOMEBREW_PREFIX/opt/openjdk@21/libexec/openjdk.jdk /Library/Java/JavaVirtualMachines/openjdk-21.jdk
export JAVA_HOME=/usr/local/Cellar/openjdk@21/21.0.5/libexec/openjdk.jdk/Contents/Home

sudo ln -sfn /usr/local/opt/openjdk@17/libexec/openjdk.jdk /Library/Java/JavaVirtualMachines/openjdk-17.jdk
export JAVA_HOME=/usr/local/Cellar/openjdk@17/17.0.13/libexec/openjdk.jdk/Contents/Home

### Mongod 
brew services start mongodb-community@8.0
brew services stop mongodb-community@8.0

