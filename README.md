# MicroStream Spring Boot Usage Patterns

Patterns in using MicroStream's Spring Boot integration.


This repository contains a few example projects that demonstrate how MicroStream and
the MicroStream Spring Boot integration can be used in a project.

It is not the idea to have all best practices around application development covered
in these examples but only some patterns that can be used to work with the Object Graph that
makes your database within the JVM heap (and persist data with MicroStream).  
So aspects like security and observability are not covered but are also not affected by using MicroStream in your Spring Boot application.

## General concepts

The following are a few general concepts that are applied within the examples.

- The examples are mainly centered around using REST endpoints to process user requests.
- The *Controller* classes are responsible for defining the REST endpoint signatures (HTTP method, URLs, ...) and some validation on the received data (structures). So the _Controller_ is not responsible for validating the business rules (does the user exists, can the customer place an order, ...) but merely some validation of the received values/structures (Are values in a certain range like non-negative for age, does the supplied JSON has a property email, ...)
- The *Repository* classes are responsible for validating the business rules of your application.  They should not have any REST-specific relation so in case there is a problem with a request, a RuntimeException-based exception can be thrown.

## Example

The example supports (in a limited way) the management of a library.  You have the concept books and users who can lend a book.

You can perform the following actions

- Retrieve all known books.
- Retrieve all known users.
- Add a new User.
- Update the email address of a User.
- Retrieve the books assigned to a user.
- Assign a book to the user.

Although the application is still limited in functionality, it is already a bit more elaborated than a hello-world style application. With a hello-world style example, we would have the danger of showing patterns that are not applicable to the real world.

## Foundation example

(See directory _storage-foundation_)

The MicroStream Spring Boot integration exposes a Spring bean that implements the *EmbeddedStorageFoundation* interface.  The Storage Foundation has used the configuration values that Spring has found in your environment.

Within the example, the configuration values are placed within the _application.properties_ file (standard properties file for Spring Boot) and can be used to determine the location of the storage on disk, number of channels etc ...

Based on this Storage Foundation, the actual *StorageManager* is started with some additional configuration by the developer. Within the class *DataConfiguration* some examples are given of the customization the developer can make to the _StorgeManager_.  It includes the definition of custom Type handlers and Legacy Type handlers.

The method *defineStorageManager()* also checks if there is already a _Root_ object available within the Storage Manager. If that is not the case, the _Root_ object needs to be initialised and in this example, some initial data are also created (the database is populated with initial data).
Also, the _Root_ object is supplied with the _StorageManager_ so that changes can be stored when they are made to the _Root_ object.

Since we should not return modifiable collections from the _Root_ object, as that would allow for changes to the database by other parts of the application without storing the changes to the external storage, the Root object needs to access the *StorageManager.store()* method for all methods that modify the _database_.

Each Repository Spring bean has a constructor that takes the _StorageManager_ as a parameter.  The _Root_ object is retrieved from this parameter so that the _Repository_ code can delegate actions on the _database_ to the _Root_ object.

Have a look at the file _commands.txt_ for the example of CURL commands that the application support.

# Plain

Without the integration code, see directory _plain_.

You can compare the previous example with the code where we do not use the MicroStream Spring Boot integration.  The only difference is in how the configuration values for the _StorageManager_ are retrieved.


Within the class *DataConfiguration* we now create an *EmbeddedStorageFoundation* instance ourselves instead of letting the integration code do this for ourselves.  The configuration is read from a properties file and the location of that file is retrieved from the Spring Boot configuration.  Of course, we might also read the individual configuration values from the Spring Boot configuration just as the integration does.

Other than this, the project code is identical to the previous example.

# Lazily started StorageManager

See directory _lazy_.

Within the storage foundation example, the MicroStream Storage manager is started when the Spring Boot application starts.  This is because the bean is also injected into the repositories that are created at boot time.

There are 2 options to avoid this when you don't want or can't start the _StorageManager_ when the application is started.

Use the Spring Boot configuration parameter to start all Beans lazily.

````
spring.main.lazy-initialization=true
````

This option is put in a comment into the _application.properties_ file of the _storage-foundation_ example so that you can test it out.

In this case, all Spring beans are only created when they are accessed. In the case of the example, this means that the _StorageManager_ is only started with the first user request.

If you don't want to have all Beans lazily created, you can make use of the @Inject  Provider class that is supported by Spring.

An example is created in this _lazy_ program and you can find the following construct in the *UserRepository*:

````
public UserRepository(Provider<StorageManager> storageManagerProvider) {

    this.storageManagerProvider = storageManagerProvider;
}

private Root getRoot() {
    return (Root) storageManagerProvider.get().root();
}
````

We do not use the _StorageManager_ itself in the constructor but ask Spring for an implementation of a _Provider_ that will give us the Bean later on.

When we need to access the Root object, we actually ask the _StorageManager_ from the provider and get to the Root object. So only when processing the user request, the StorageManager is initialized (if not done already previously), and it is not started at application startup.


Do realise that starting the _StorageManager_ might be required for your use case when not all resources are available at application startup (like a database if you really need to use the database as storage target with MicroStream) it has a performance impact on the first user request as the _Storagemanager_  loads the Root object data at that moment.
