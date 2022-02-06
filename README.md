# Social Network SoNet
Semestrální práce z předmětu KIV/PIA, 2021/2022. Cílem práce bylo implementovat sociální síť.  
Zadání: https://github.com/osvetlik/pia2020/tree/master/semester-project.

## Uživatelská příručka
Aplikace obsahuje celkem pět oddílů:

1. **Home** - Uvítací stránka. Zde je možné se přihlásit po kliknutí na _Log in_ nebo zaregistrovat po 
kliknutí na _Sign up_. Po přihlášení je možné se odhlácit po kliknutí na _Log out_
2. **Wall** - Tato stránka obsahuje příspěvky uživatele, jeho přátel a oznámení od administrátorů. 
Pod příspěvky je okno, kde je možné napsat vlastní příspěvek. Administrátoři mohou zaškrtnout chackbox _Announcement_,
aby jejich příspěvek byl počítán jako oznámení.  
Dále obsahuje seznam přátel a zda jsou aktivní. S online přáteli lze otevřít chat, ale pouze pokud uživatel 
nebo přítel nemá již otevřený jiný chat. Okénko chatu se objeví vespod stránky pod příspěvky.
3. **Friends management** - Zde je možné vyhledat uživatele, které je pak možné přidat do přátel 
(druhý uživatel musí žádost přijmout), přijmout žádost o přátelství jiného uživatele nebo uživatele zablokovat.
Do pole vyhledávání je nutné napsat alespoň 3 znaky. Po vyhledání se vypíší všichni uživatelé začínající na 
tento řetězec.  
Níže jsou čtyři oddíly zobrazení:
   * Friends - Zobrazuje všechny přátele a jejich aktivitu. Přátele je zde možné odebrat z přátel
   * Blocked users - Zde jsou všichni zablokovaní uživatelé. Je možné je zde odblokovat.
   * Sent requests - Zde jsou všechny žádosti o přátelství jinému uživateli. Tuto žádost je zde možné zrušit.
   * Friend requests - Zde jsou všechny žádosti o přátelství od jiných uživatelů. Žádosti je
   možné zde přijmout nebo odmítnout.
4. **Administration** - Tato stránka je přístupná pouze uživatelům s administrátorskými právy. 
Je zde seznam všech uživatelských loginů a jejich role. Administrátor může dávat či brát administrátorská práva
a vytvářet nové uživatele.
5. **Settings** - Zde je možné upravit své osobní údaje.

## Sestavení a spuštění aplikace
Pro sestavení a spuštění aplikace je nutné provést následující kroky:
1. Provést _checkout_ projektu z GITu
2. Spustit Docker
3. V root složce projektu zadat příkaz přes command line: `docker build -t pia/pruchapa .` (build aplikace)
4. V root složce projektu zadat příkaz přes command line: `docker-compose up` (spuštění aplikace a databáze)
5. Aplikace bude přístupná na adrese: http://localhost:8080/
6. Příkaz pro odstranění docker containeru s aplikací a databází: `docker-compose down`, případně
   `docker-compose down -v` pro odstranění DB volume s daty

Po prvním spuštění aplikace jsou v databázi vytvořeny uživatelské účty:
* login: admin@init.com, heslo: admin, role: ADMIN
* login: user@init.com, heslo: user, role: USER

## Realizace Bonus part

Ze sekce Bonus part bylo implementováno:
* generování kódu přes OpenAPI/Swagger
* likes  

