# SakaiClientAndroid

### Status: [In Production](https://play.google.com/store/apps/details?id=com.sakaimobile.development.sakaiclient20&hl=en_US) with over 3,500+ monthly active users ([proof](https://github.com/SChakravorti21/SakaiClientAndroid/blob/master/imgs/Sakai_Mobile_Adoption.png))

A native mobile client for Rutgers Sakai on Android (iOS version: https://github.com/PRAN1999/SakaiClientiOS)

If you're not familiar with Sakai or want to know more about the features of Sakai Mobile, check out our website: https://rutgerssakai.github.io/SakaiMobile/

## Motivation

Sakai is the learning management system used by Rutgers, so it is the platform by which professors distribute grades, announcements, resources, etc. Unfortunately, we feel that the mobile version of Sakai's website is very difficult to use, and we heard the same complaint from our peers as well. In order to address this issue, we built native Android and iOS applications that interface with the Sakai APIs and provide all the same functionality, but with a much better user experience on mobile devices.

## What we learned

Although Sakai Mobile is specifically a mobile application, we learned a lot about general software engineering concepts:
- Dependency injection and inversion of control (Dagger 2)
- Reactive programming (RxJava/RxAndroid, Android architecture components like the ViewModel)
- Structured data storage and retrieval with an RDBMS (SQLite proxied by the `Room` Android architecture component)
- Architecting the application in an extensible manner (we used the Model-View-ViewModel pattern to quickly iterate on features)
  - Using the `Repository` pattern to unify persisted data and fresh data from network requests
- Metrics, Analytics, Crash Reporting (Fabric/Firebase Analytics)
- Secure, cookie-based authentication to avoid locally persisting students' credentials

*_This README is a work-in-progress_*
