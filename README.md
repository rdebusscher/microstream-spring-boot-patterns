# MicroStream Spring Boot Usage Patterns

The **v2** directory contains the examples with Spring Boot 2.x. **v3** is for Spring Boot 3.x


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

# Proposed changes

The following is the list of proposed changes to make the code (of your application) better structured and integration with MicroStream easier.

The examples require the code from https://github.com/microstream-one/microstream/pull/390 to compile and work.

# Foundation customizer

See directory _foundation-customizer_

The new version of the integration has implemented the following steps so that you can directly use a _StorageManager_ based Spring bean. (and as the developer keep full control of customizations and initializations)

- Build `EmbeddedStorageFoundation` from the Configuration values
- Allow customizations by the developer `EmbeddedStorageFoundation` using `EmbeddedStorageFoundationCustomizer`
- Integration creates the `StorageManager`
- Allow initialization of the `StorageManager` (like adding initial data when storage is created at the first run) through `StorageManagerInitializer`.

The code within the `DataConfiguration` of the _storage-foundation_ becomes now better structured and results in the classes `FoundationCustomizer` and `RootPreparation`.

```
@Component
public class FoundationCustomizer implements EmbeddedStorageFoundationCustomizer {

    @Override
    public void customize(EmbeddedStorageFoundation embeddedStorageFoundation) {
      // Do customization
    }
}
```

```
@Component
public class RootPreparation implements StorageManagerInitializer {

    private static final Logger LOGGER = LoggerFactory.getLogger(RootPreparation.class);

    @Override
    public void initialize(StorageManager storageManager) {
       // Check if Root is available (and assign if needed) and add initial data if needed.
    }
}    
```

The addition of these 2 interfaces and using them when the Spring Bean for the _StorageManager_ is created, makes the code better structured as each class has its own purpose.

# Root Spring Bean

See directory _root-bean_.

Within the _Repository_ beans, we accessed the Root object through the _StorageManager_ bean.  Although this works, it is a bit cumbersome to always retrieve the root in that way.

With this updated version of the integration, the Root object can also be turned into a Spring Bean by annotation it with `@Storage`.

```
@Storage
public class Root {

    @Autowired
    private transient StorageManager storageManager;
```

The _storage_ annotation is a custom annotation that is both a `@Component` and `@Qualifier`.  That way it can be detected by the Spring Boot integration and made sure that it is a Spring Bean but also correctly registered with the Storage manager.

Since it is a Spring Bean, you can also inject other beans into it, like the _StorageBean_. Since the integration is responsible for creating the Root object instance if needed (through a special factory method) using standard Java constructs, only Field and Setter injection is allowed (and not constructor injection)

# Multiple Managers

See directory _multiple-managers_

This requires version 8.0 (or [this PR](https://github.com/microstream-one/microstream/pull/490))

There might be situations in that you want to make use of multiple _StorageManager_s. Just like you have applications that talk to multiple databases. This is also possible with MicroStream. And with version 8.0 there will be support within the Spring Boot integration to define multiple Storage Managers that are integrated as Spring Beans.

If you have multiple beans of the same type, you need to make a distinction between them through the use of _Qualifiers_ in Spring.  Also in case you want to have multiple Storage Managers. Since we cannot know the name of the labels you want to give each StorageManager, you need a little Configuration Bean to configure the beans. But you can make use of a _Provider_ from the integration code so that the amount of code that you need to write is very limited.

```
@Configuration
public class DefineStorageManagers {

    private final StorageManagerProvider provider;

    public DefineStorageManagers(StorageManagerProvider provider) {
        this.provider = provider;
    }

    @Bean(destroyMethod = "shutdown")
    @Qualifier("green")
    public EmbeddedStorageManager getGreenManager() {
        return provider.get(DatabaseColor.GREEN.getName());
    }

    @Bean(destroyMethod = "shutdown")
    @Qualifier("red")
    public EmbeddedStorageManager getRedManager() {
        return provider.get(DatabaseColor.RED.getName());
    }
}
```

You can freely choose the name of the class, as long as you annotate it with the `@Configuration`Spring annotation. You need to inject the `StorageManagerProvider` bean that is available through the integration code so that you can call the method `.get(qualifier)` on it to instantiate and expose a `StorageManager`.  The qualifier label and the method parameter should match (for your own ease). Within the code, I made use of an enum but an annotation member doesn't allow it since you can only provide constants.

The qualifier label is also used as a prefix to look up the configuration values. For the above example, you could have the following entries in the configuration file

```
one.microstream.red.storage-directory=red-db
one.microstream.red.channel-count=2

one.microstream.green.storage-directory=green-db
one.microstream.green.channel-count=1
```

But of course, all storage types (disk, database, etc ...) are supported, just as we have seen earlier. Don't forget to include also the qualifier label as part of the configuration key as shown in the example.

Also, the `@Storage` annotation and `StorageManagerInitializer` and `EmbeddedStorageFoundationCustomizer` concepts are supported when you make use of multiple _Storage Managers_.

In the case of the @Storage annotation, also add the correct `Qualifier` annotation. That way, the integration knows which class it needs t associate with which Storage Manager.

```
@Storage
@Qualifier("red")
public class Products {
```

And when you have defined a bean that implements `StorageManagerInitializer` and `EmbeddedStorageFoundationCustomizer`, you can find out based on the `databaseName` of the _Foundation_ of the _StorageManager_ which variant is passed in.  An example is

```
@Component
public class RootPreparationOfRedDatabase implements StorageManagerInitializer {

    @Override
    public void initialize(StorageManager storageManager) {
        if (!DatabaseColor.RED.getName().equals(storageManager.databaseName())) {
            // This customizer operates on the Red database
            return;
        }
        // Perform the required initialisation for the Red root = Products
    }
}
```

# Multiple Manager (variation)

See directory _primary_

Instead of defining 2 _named_ managers, you can also make use of the _primary_ manager that is available and use it when you only need one manager.

The changes that are required to use this variation with the previous multiple manager's example, are minimal.

You only have to define one manager within the `DefineStorageManagers` configuration class. For example, you can remove the definition of the Red variant and only keep the Green one (and call it maybe _secondary_.

This change needs to be reflected in the configuration keys.  The _Primary_ storage manager reads the keys without a label, so we only need to have the `one.microstream.` prefix.  For the secondary, we still need the correct label which is the value of the parameter we use when we call `StorageManagerProvider.get()`.

If we define a Root object through the `@Storage` annotation, we don't need to specify a Qualifier if it is the root for the primary _StorageManager_. We only need it for additional ones, like the secondary.

And when we have implemented classes that implement the `StorageManagerInitializer` or `EmbeddedStorageFoundationCustomizer` interfaces, we must use the _'Primary'_ as the value for the database name to detect if the methods are called for the Primary _StorageManager_.


# Dev Mode

See directory _dev-mode_

With the changes of https://github.com/microstream-one/microstream/pull/518, it is no longer needed to define the MicroStream jars as part of the _restart classloader_ to have support for reloads.

# Spring Cache integration

See directory _cache_

MicroStream can be used as Spring Cache provider and the cache values will be persisted so that you have an already filled cache when your application starts. The application within this directory shows an example how you can achieve this.

In addition to the _microstream-integrations-spring-boot_ artefact, you also need to add the _microstream-cache_ to have the Cache API and MicroStream implementation code available within your project. For Spring Boot, you need the _spring-boot-starter-cache_ artifact.

Following configuration steps are needed.

Define the annotation `@EnableCaching` on the Spring Boot application class, together with the `@SpringBootApplication` annotation.

Define a Spring Bean that implements the interface `org.springframework.boot.autoconfigure.cache.JCacheManagerCustomizer`. You can inject the `EmbeddedStorageManager` that is created by the integration code and configure within the `customize()` method the caches. For each cache you need to specify the name and the Expiry Policy.  Have a look at the `CacheSetup` class.

```
    private void defineCache(CacheManager cacheManager, String cacheName, Duration duration) {
        CacheConfiguration<?, ?> configuration = CacheConfiguration
                .Builder(Object.class, Object.class, cacheName, storageManager)
                .expiryPolicyFactory(CreatedExpiryPolicy.factoryOf(duration))
                .build();

        cacheManager.createCache(cacheName, configuration);
    }
```

The built in `SimpleKey`implementation that is used for the cache key is not useable with MicroStream. Its `hashCode` value is calculated when the instance is created and stored in a _transient_ field. This transient value is not persisted by MicroStream and thus all the key values loose their hash value which is used to lookup values. So after a restart of the applications, no keys are detected anymore and it appears that the cache is empty.  To Solve this you need to create a custom key generator.

```
public class CustomKeyGenerator implements KeyGenerator {
    @Override
    public Object generate(Object target, Method method, Object... params) {
        // Key should only depend on parameters so that @Cacheable and @CacheEvict annotated methods result in same key
        return "Key" + StringUtils.arrayToDelimitedString(params, "_");
    }
}
```

This key generator is picked up by defining a class like `CacheConfig`

```
@Configuration
public class CacheConfig implements CachingConfigurer {

    public KeyGenerator keyGenerator() {
        return new CustomKeyGenerator();
    }
}

```

The above steps are needed to make use of the `@Cacheable` and `@CacheEvict` on Spring bean methods to have automatic caching functionality.

Please note that you can't combine the Cache functionality through MicroStream and using a Root object together. You need different Storage Managers for that purpose. Within version 8.0 of MicroStream, you can define multiple managers more easily.




