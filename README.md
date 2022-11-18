# Oppsett

- git clone dette prosjektet
- Hvis du ikke har IntelliJ, last ned community-versjon her: https://www.jetbrains.com/idea/download
- I IntelliJ: "Open" og velg mappa du har klonet til
- IntelliJ pleier å starte et gradle-bygg som laster ned alle avhengigheter, vent til det er ferdig.
- Naviger til `Main.kt` ("Project"-fanen helt til venstre -> wordfeud-strategy/src/main/kotlin/Main) 
- Bytt ut "\<botname\>" med navnet på boten din (fra listen under). 
- Trykk på den grønne play-knappen ved siden av main-funksjonen. Hvis det printes: "Logged in as ..." så er alt tipp topp!
  (Ser du ingen grønn play-knapp kan du høyre-klikke på kotlin-mappa under src/main, og velg "Mark Directory as" -> "Sources Root")
- Trykk på den røde stop-knappen oppe til høyre, så du ikke spammer Wordfeud-APIet mer enn nødvendig 😬 , så er du klar for faggruppemøtet! 🎉

**Wordfeud-brukere:**
- Even: evenbot
- Kristoffer Severinsen: severinsenbot
- Kristoffer Stensen: stensenbot
- Sondre: sondrebot
- Stian: stianbot
- Thor Kristian: thorkbot
- Torbjørn: torbjornbot
- Vegard: vegardbot


# Intro

Utgangspunktet vårt er en Wordfeud-bot som alltid finner det høyest scorende legget! 
Boten er bygget på en algoritme som ble beskrevet i artikkelen [The World’s Fastest Scrabble Program](https://www.cs.cmu.edu/afs/cs/academic/class/15451-s06/www/lectures/scrabble.pdf) helt tilbake i 1988 🤯
Det er en ganske snedig algoritme som finner alle gyldige legg. Med noen enkle steg reduseres problemet med å finne alle legg til én dimensjon, slik at man bare trenger å finne alle gyldige legg for én enkelt rad om gangen. I Wordfeud kan man enten legge brikker horisontalt eller vertikalt, men dersom man tenker på et vertikalt legg som et horisontalt legg på et transponert brett, trenger man bare å implementere algoritmen for å finne alle gyldige horisontale legg.

Boten har en kjempefordel med at den kan alle ord i ordlista, og at den også finner alle stedene den kan legge disse ordene.
Strategien med å alltid legge høyest scorende legg gir en ganske god bot (dere kan jo prøve å slå den selv... 😱), men det er absolutt rom for forbedringer!
Og det er det vi skal se på i dag!

Du skal implementere funksjonen `makeTurn` i fila `MyBot.kt`.
Den tar inn et parameter `game` som gir deg:
- `board` (oversikt over hvilke brikker som allerede ligger på brettet)
- `rack` (hvilke brikker du sitter med)
- `score` / `opponentScore` (stillingen)
- `scorelessTurns` (ved 3 scorelessTurns avsluttes spillet, og begge spillere mister poengsum tilsvarende det som de har igjen på racket)

Funskjonen returnerer en `Turn` som kan være av følgende tre typer:
- `MOVE`: du legger brikker på brettet og får poeng 🎉
- `SWAP`: du bytter én eller flere av brikkene du har på racket ditt (dette er bare lov om det er minst 7 brikker igjen i posen)
- `PASS`: du passer

## The real deal / simulation
Boten kan kjøres i 2 forskjellige modi:

### The real deal
I denne modusen logger boten seg på Wordfeud-APIet og kjører en loop hvor den venter på tur i pågående spill, og utførerer trekket som blir returnert av `makeTurn`-funksjonen. 

Jeg har laget en klient på https://algpip.netlify.app/ som dere kan bruke til å starte matcher mellom botene deres ⚔️

### simulation
Enkeltkamper kan ofte avgjøres av tilfeldigheter (hvilke brikker du trekker). I denne modusen spiller tilfeldighet en mindre rolle, ettersom du kan simulere x antall kamper mot en 'controlBot'.
Dette kan være en fin måte å finne ut om en ny strategi er gunstig eller ikke...
Simulering startes ved å kommentere ut 'The real deal' i `Main.kt`.

Digital blomst 🌻 til den som klarer å oppnå høyest seiersprosent på 100 runder! 
(én runde tilsvarer 2 kamper, med samme brikkefordeling, hvor hver bot får begynne én kamp hver)
For hver kamp som simuleres printes også brikkerekkefølgen for den kampen, så om man ønsker å re-simulere en gitt kamp for å sjekke om utfallet blir noe annet med en annen strategi, kan man lime inn brikkerekkefølgen som parameteret `letterDistribution` til `Simulator`-konstruktøren.

# Strategi

Ulike strategier kan være gunstige i ulike situasjoner...
Hva tjener man mest på? Generelle strategier som kanskje gjør boten **litt** bedre i hver kamp? Eller spesifikke strategier som løser enkeltsituasjoner veldig godt..?
Her følger en rekke ting det kan være lurt å tenke på!

### Board position
En ting er jo hvor mye poeng du selv får, men det er vel så viktig å tenke på hvor mye motstanderen får.
Et åpent brett gir generelt flere muligheter for å oppnå høye poengsummer, mens man på et lukket brett gjerne får lavere poengsummer.
Normalt sett vil derfor den som leder ønske et mer lukket brett, mens den som ligger under vil håpe på et åpent brett.

Det er essensielt å utnytte seg av bonusfeltene 
- `TW` (trippelt ordpoeng) 
- `DW` (dobbelt ordpoeng) 
- `TL` (trippelt bokstavpoeng) 
- `DL` (dobbelt bokstavpoeng).

Og tilsvarende skummelt å gi motstanderen muligheten til å utnytte dem. 

Ekstra mye poeng blir det om man treffer 
- `DW` & `DW` (4 * ordpoeng 💰)
- `DW` & `TW` (6 * ordpoeng 💰💰) 
- `TW` & `TW` (9 * ordpoeng 💰💰💰)

Enkelte brikker som W, C, Æ, Y kan gi veldig mye poeng om man får dem på bonusfeltene, selv med korte ord som WC, WU, CV, CD, PC, ÆS, ÆR, ÆH, HÆ, NÆ 

### Leave
Brikkene du blir sittende igjen med etter et legg, eller et bytte, kalles leave.
Jo bedre brikker du sitter igjen med, jo lettere er det å gjøre et godt legg i neste runde.
Om man blir kvitt alle brikkene sine i ett legg, en såkalt bingo, får man 40 ekstrapoeng.

Man ønsker altså å få så mange poeng som mulig, samtidig som man blir sittende igjen med gode brikker.
Noen ganger lønner det seg å bytte brikker. Men når lønner det seg? Og hvilke brikker skal man bytte?

`E, R, A, N, S, T, L, I, K, O, G, D, U, EE, M, V, B, F, P, Ø, Y, H, Å, RR, TT, AA, NN, J, SS, LL, KK, II, GG, EEE, Æ, OO, DD, PP, C, MM, BB, UU, TTT, VV, FF, NNN, AAA, SSS, W`

Denne rekkefølgen sier noe om hvilke brikker som oftest forekommer i en bingo. De første bokstavene er de som forekommer oftest. Det er ofte gunstig å ikke bli sittende med flere av samme bokstav, men det finnes f.eks. flere bingoer med to E-er (EE) enn med M.

Om man ikke passer seg er det fort gjort å havne i såkalt vokalnød. Da blir det vanskelig å gjøre gode legg, og man blir fort nødt til å gjøre et bytte. En bingo har i snitt 2.66 vokaler (38%). I starten av spillet er 36% av brikkene man kan trekke vokaler.
For mange vokaler er heller ikke gunstig.

### End game
Når det er mindre enn 7 brikker igjen i posen er det ikke lenger lov å bytte. Det kan derfor være lurt å følge med på hvor mange brikker som er igjen, så man rekker å gjøre de byttene man ønsker.

Når det er få brikker igjen, kan det også gi mening å regne på sannsynligheten for at motstanderen sitter med spesifikke brikker, for å vite om det er lurt å åpne/eller sperre for disse brikkene.

Kanskje er det lurt å sørge for at det ligger minst én brikke igjen i posen, så motstanderen ikke kan gå ut med en bingo?

Når posen er tom vet man hvilke brikker motstanderen sitter igjen med, og man har derfor muligheten til å spille perfekt.
Kanskje bør man legge et legg som gir veldig lite poeng, men som blokker en stor åpning for motstanderen? Eller kanskje til og med hindrer mulighetene for å legge ut en brikke i det hele tatt? Kanskje er det best å legge én og én bokstav fremfor å bruke alle på en gang? Eller kanskje man skal lage en åpning som bare man selv kan utnytte?

Hvis man legger sine siste brikker på brettet avsluttes spillet, og man får ekstra poeng tilsvarende poengsummen til brikkene som motspiller sitter igjen med. Disse poengene får i tillegg motspiller i minus, så her er det potensielt mye å tjene.

I noen få tilfeller kan det faktisk også lønne seg å passe.
F.eks om det har vært to scoreless turns, og du har en tilstrekkelig ledelse til å vinne (spillet avsluttes ved tre scoreless turns), eller dersom de eneste leggene du kan gjøre fører til at motstanderen får flere poeng enn de hadde gjort om du passet.

### Annet
Den blanke brikken (`*`) regnes som den beste brikken. Man får ingen poeng for den blanke brikken, men den gjør det mye lettere å oppnå høye poengsummer, f.eks ved å få lagt bingo. Når bør man bruke den blanke brikken, og når bør man spare den til senere?

Første legget får man som regel ikke så veldig mye poeng for, kanskje bør man senke terskelen for å gjøre et bytte her?

Hvilke ting du velger å fokusere på er opp til deg, kanskje du kommer på andre måter du kan sørge for at din bot blir helt uslåelig? 

Lykke til! 😎
