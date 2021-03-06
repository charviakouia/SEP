# Changelog

## Feinspezifikation - 3. Meilenstein

* mehr Links im Header, mögl. durch Drop-Downs
* (not implemented yet probably) LendingPeriodViolations accessible to staff now too
* Erstellung der Klasse `Pagination`
* Umbennung von `UserDao`s `readUserBySearchCriteria` zu `searchUsers`
* rename `UserSearch.searchUser` -> `UserSearch.searchUsers`
* rename `MediumSearch.searchMedium` -> `MediumSearch.searchMedia`
* user search: filter by user role
* `UserSearchDto#userLendStatus` -> `UserSearchDto#lendStatus`

## Feinspezifikation – 2. Meilenstein

* Erstelle die Validatoren `LendingProcessSignatureValidator`, `ReturnFormSignatureValidator` und `MediumCreationSignatureValidator`,
  welche bei manchen Facelets benötigt werden. 
* `CopiesReadyForPickup.{getAttributeName,goToLending}`
* `MediumDao.getAllMediumAttributes`
* change `AttributeOrCategory{,Converter}` to `SearchCriterion{,Converter}`

## Feinspezifikation – 1. Meilenstein

* Verwende bei jedem Facelet / jeder Backing Bean, welche Bedienelemente abhängig vom Nutzer ein- oder ausblendet,
  eigenständige Methoden, deren Namen per Konvention mit `showing` beginnen, anstatt diese Business-Logik direkt
  via EL in das Facelet zu schreiben.
* Nenne das BB `DirectLending` in `Lending` und das Facelet `direct-lending` in `lending` um.
  Das Präfix _direct_ trägt nicht zum Verständnis bei und ist inkonsistent mit `ReturnForm`.
* Nenne das Unterpaket `backingbeans` in `backing_beans` um, um der Namenskonvention zu folgen (engl. _backing bean_ wird auseinander-
  geschrieben und deswegen im snake case einen Unterstrich enthalten).
* Erstelle die statische Methode `readCopiesReadyForPickupAllUsers` in `MediumDao`, die bei dem Facelet `copies-ready-for-pickup-all-users`
  benötigt wird.
* Erstelle Methoden wie `getAllUserOperators` (Muster: `getAll…`) bei gewissen BBs, die an die statische Methode `values` von Enumerationen
  delegieren. Letztere sind in der EL nicht referenzierbar
* Erstelle die Methode `UserRole.isStaffOrHigher`. Diese ist lediglich eine Utility-Methode, die in mehreren BBs verwendet wird.

