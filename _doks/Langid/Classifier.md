#Classifier
Classifier-klassa tek inn eit String-objekt og analyserer det. Dersom teksten er meir enn 300 ord lang
så blir han analysert av eit bibliotek som heiter Langid. Dette returnerer språket gjenkjent og sannsynet
for at dette språket er riktig. Dersom teksten er under 300 ord tek Shortclassifier seg av han.
Shortclassifier ser igjennom teksten etter ord unike for utenlandske språk og sender teksten videre
til Langid om han finn nokon. Om ikkje så ser Shortclassifier etter endingar unike for Nynorsk og Bokmål og 
reknar ut andelen av gjenkjente ord frå dei to. Om det er meir enn 70% nynorsk blir det anteke at teksten er nynorsk.

Classifier tek resultatet frå shortclassifier og langid og lagar eit *AnalyzedText*-objekt. Dette objektet inneheld
resultatet fra shortclassifier, samt ei analyse av kompleksiteten i teksten. Denne analysa tel antal ord, antal ord som har meir enn 5 bokstavar og antall stopp-symbol(":","." osv) og reknar ut ein skår(LIX) for kompleksitet. Deretter blir eit objekt med LIX og antall ord m.m., sannsynet for gjenkjent språk og funne språk returnert som eit *AnalyzedText*-objekt.

#Instillingar
Shortclassifier les når det instansierast fyrst ei fil, "resources/config.ini", som inneheld informasjon om endingar og ord som skal leitast etter. Linjer som begynner med "_" definerar kva for ord som kjem. Startar ei linje med "_endinger_bm" lagrast videre ord som bokmålsendingar, fram til ei ny linje startar med "_". Denne informasjonen lagrast i eit *RuleSet*-objekt Andre filar kan brukast ved hjelp av å kalle funksjonen *loadConfig* med ein filbanen og namnet på 'ordboka'.
