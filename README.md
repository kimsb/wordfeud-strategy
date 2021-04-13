# Hjemmelekse

- git clone dette prosjektet
- Hvis du ikke har IntelliJ, last ned community-versjon her: https://www.jetbrains.com/idea/download
- I IntelliJ: "Open" og velg mappa du har klonet til
- Velg "File" -> "Project Structure" og under "Project SDK" velger du versjon 11. Har du ingen versjon 11 i nedtrekkslista velger du "Add SDK" -> "Download JDK" og velger en versjon 11
- Helt til høyre i IntelliJ er det en liten fane hvor det står "Maven", åpne den og trykk på "Reload all Maven Projects" (det resirkulerings-ikonet)
- Naviger til Main.kt ("Project"-fanen helt til venstre -> wordfeud-strategy/src/main/kotlin/Main) 
- Bytt ut "\<botname\>" med navnet på boten din (fra listen under). 
- Trykk på den grønne play-knappen ved siden av main-funksjonen. Hvis det printes: "Logged in as ..." så er alt tipp topp!
- Trykk på den røde stop-knappen oppe til høyre, så du ikke spammer Wordfeud-APIet mer enn nødvendig 😬 , så er du klar for faggruppemøtet! 🎉

**Wordfeud-brukere:**
- Asbjørn: asbjornbot
- Emil S: staursetbot
- Emil L: lundebot
- Hege: hegebot
- Joel: joelbot
- Kristian: kristianbot
- Kristoffer: kristofferbot
- Linus: linusbot
- Marius: mariusbot
- Ole-Martin: ole-martinbot
- Simen E: endsjobot
- Simen S: stoabot
- Torbjørn: torbjornbot
- Øyvind: oyvindbot


# Intro

Utgangspunktet vårt er en Wordfeud-bot som alltid finner det høyest scorende legget! 
Boten er bygget på en algoritme som ble beskrevet i artikkelen [The World’s Fastest Scrabble Program](https://www.cs.cmu.edu/afs/cs/academic/class/15451-s06/www/lectures/scrabble.pdf) helt tilbake i 1988 🤯
(Det er en ganske snedig algoritme som finner alle gyldige legg. Med noen enkle steg reduseres problemet med å finne alle legg til én dimensjon, slik at man bare trenger å finne alle gyldige legg for én enkelt rad om gangen. I Wordfeud kan man enten legge brikker horisontalt eller vertikalt, men dersom man tenker på et vertikalt legg som et horisontalt legg på et transponert brett, trenger man bare å implementere algoritmen for å finne alle gyldige horisontale legg.)

Boten har en kjempefordel med at den kan alle ord i ordlista, og at den også finner alle stedene den kan legge disse ordene.
Strategien med å alltid legge høyest scorende legg gir en ganske god bot (dere kan jo prøve å slå den selv... 😱), men det er absolutt rom for forbedringer!
Og det er det vi skal se på i dag!

Du skal implementere funksjonen 'makeTurn' i fila MyBot.kt.
Den tar inn et parameter 'game' som gir deg:
- board (oversikt over hvilke brikker som allerede ligger på brettet)
- rack (hvilke brikker du sitter med)
- score / opponentScore (stillingen)
- scorelessTurns (ved 3 scorelessTurns avsluttes spillet, og begge spillere mister poengsum tilsvarende det som de har igjen på racket)

Funskjonen returnerer en 'Turn' som kan være av følgende tre typer:
- MOVE: du legger brikker på brettet og får poeng 🎉
- SWAP: du bytter x antall av brikkene du har på racket ditt (dette er bare lov om det er minst 7 brikker igjen i 'bag')
- PASS: du passer

SWAP og PASS er scoreless turns.

## The real deal / Simulation
Boten kan kjøres i 2 forskjellige modi:

### The real deal
I denne modusen logger boten seg på Wordfeud-APIet og kjører en loop hvor den venter på tur i pågående spill, og utførerer trekket som blir returnert av 'makeTurn'-funksjonen. 

Jeg har laget en klient på https://algpip.netlify.app/ som dere kan bruke til å starte matcher mellom botene deres ⚔️

### Simulation
I denne modusen simuleres x antall kamper mot en 'controlBot'.
Dette kan være en fin måte å finne ut om en ny strategi er gunstig eller ikke...
Simulering startes ved å kommentere ut 'The real deal' i Main-fila.

Digital blomst 🌻 til den som klarer å oppnå høyest seiersprosent på 100 runder! 
(én runde tilsvarer 2 kamper, med samme brikkefordeling, hvor hver bot får begynne én kamp hver)

# Mulige strategier
