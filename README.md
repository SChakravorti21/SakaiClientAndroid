# SakaiClientAndroid

**In Progress** A mobile client for Rutgers Sakai on Android (iOS version: https://github.com/PRAN1999/SakaiClientiOS)

### Note:
The architecture for this application is currently being worked on. This is mainly because the application backend needs to be a lot more robust and unit-testable, as well as resilient to our changing requirements. Among the changes include:
  - A proper method of persisting data with Room
  - Moving over to using RxJava with Retrofit (and Room)
  - Using proper dependency injection with Dagger 2 to simplify management of 
    the application's dependency graph
  - Using the repository pattern to unify persisted data and fresh data from network requests
  - Just overall separation of concerns (we've been getting a lot of merge conflicts lately,
    which indicates that we don't have a good separation of concerns, although this partly
    stems from the Sakai API having some limitations that we're working around)
    
We plan on implementing features to help students find and manage their content more easily, such as fuzzy content search, calendar integration, and push notifications. All such requirements will require that our data sources be as robust as possible.
