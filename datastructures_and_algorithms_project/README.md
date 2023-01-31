_Tietorakenteet ja algoritmit -kurssin harjoitustyön molempien osioiden funktiot, ratkaisuperiaatteet sekä funktioiden nopeudet on kerrottu tässä tiedostossa. Harjoitustyö oli projekti, jossa harjoiteltiin mahdolisimman nopeiden algoritmien käyttöä ja toteuttamista sekä asymptoottisten nopeuksien laskemista. Ohejlma on tarkoitettu erilaisten luontokohteiden ja alueiden lisäämiseen ja käsittelyyn sekä niiden välisten reittien etsimiseen._

_Ensimmäinen osa_

Paikkojen ja alueiden tiedot on tallennettu unordered_mappeihin, sillä ne ovat normaalisti hyvin nopeakäyttöisiä tämän ohjelman tarpeissa. Näihin säiliöihin tallennetaan paikat ja alueet ID avaimena ja sisältönä on shared_ptr:ita, jotka osoittavat structeihin, jotka sisältävät tiedot nimestä, ID:stä, tyypistä, koordinaateista sekä isäalueesta ja lapsialueista area_datassa. Shared pointereita käytetään, sillä ne hoitavat muistinhallinnan helposti. Alueiden alialueita ja isiä tallentamaan on käytetty tavallisia osoittimia, sillä ne mitätöityvät vain erittäin hetkellisesti clear_all:in aikana juuri ennen omaa tuhoutumistaan eivätkä näin ollen aiheuta ongelmia.

Kaikissa paikkoja käsittelevissä metodeissa pl. tietyn järjestyksen palauttavat metodit on käytetty lähinnä STL:n valimiita metodeja find(), insert() ja erase() ja toteutukset pysyvät melko samanlaisina metodista toiseen. Findia on käytetty paljon, sillä se löytää etsityn alkion keskimäärin vakioajassa ja palauttaa iteraattorin kyseiseen alkioon, jota voi helposti käyttää hyväksi, kun tarkastellaan alkion löytymistä. Insert-metodia käytetään hieman vastaavalla tavalla, sillä sen palauttamaa bool-arvoa on helppo käyttää lisäyksen onnistumisen tarkastamiseksi. Myös insert on keskimäärin vakioaikainen. 

Paikat aakkosjärjestyksessä palauttavassa metodissa on paikat ensin sijoitettu multimapiin, jotta ne saadaan automaattisesti järjestykseen nopeudella O(n*log(n)). Tämän jälkeen paikat siirretään palautettavaan vektoriin, mikä tapahtuu lineaarisella kompleksiudella.

Kahden koordinaatin etäisyys lasketaan coord_distance funktiossa, joka toimii keskimäärin vakioajassa, kuten muutkin unordered_mapia käytävät funktiot. Oma funktio oli tarpeelllinen, sillä etäisyys lasketaan usein ja eri metodeissa.

Koordinaattijärjestyksen palauttamisessa on ajan säästämiseksi luotu private osioon oma vektori paikkajärjestystä varten ja tämä järjestetään, jos on syytä olettaa, ettei se ole jo valmiiksi oikeassa järjestyksessä. Valmis järjestys on varma, jos paikkoja ei ole lisätty ja vektori on viimeksi järjestetty tämän metodin mukaan. Paikkojen tallennukseen käytettiin vektoria, sillä sen indeksointi on nopeaa. Järjestäminen tapahtuu yksityiseen rajapintaan mmääritettyjen merge_sortin ja mergen avulla, joita on muokattu vastaamaan järjestämisen tarpeita. Järjestämisessä kestää O(nlog(n)), ja metodin suoritus valmiilla järjestyksellä on lineaarinen, sillä jokainen place_vecin alkion id on sijoitettava palautettavaan vektoriin. Hieman vastaavalla tavalla on toteutettu places_closest_to, jossa on käytetty samaa järjestämisalgoritmia, jolle tosin välitetään oikea vertailukoordinaatti. Tämänkin aikakompleksisuus on sama kuin places_coord_orderin.

Useimmat alueita käsittelevät metodit ovat toteutukseltaan vastaavia kuin paikkoja käsittelevät samaa asiaa tekevät metodit. Kuitenkin alialueiden ja isäalueiden etsintä on toteutettu rekursiivisesti etsinnän helpottamiseksi. Alueet määrittävässä structissa onkin osoittimet isään ja lapsiin liikkumisen helpottamiseksi. Nämä metodit suoritetaan pahimmillaan lineaarisessa ajassa, mutta jos vanhempia tai lapsia ei ole, metodi on vakioaikainen, sillä rekursiota ei tarvita.

creation_finished muuttaa tämän tilannetta totuusarvolla seuraavan adding_finished attribuutin arvon trueksi ja coord_orderin Not_sortediksi. Näitä tietoja tarvitaan paikkojen järjestämisessä koordinaattien mukaan ja esimerkiksi coord_order on muutettava vastaamaan järjestämätöntä tilaa, sillä on mahdollista, että place_vecin päähän on lisätty paikka edellisen järjestämisen jälkeen mutta ennen creation_finishedin suorittamista, mikä johtaisi väärään järjestykseen. Place_vecia ei pidetä aina järjestyksessä, sillä järjestämiseen kuluu aikaa, eikä järjestystä tarvita, ellei places_coord_order tai places_closest_to metodeja käytetä.creation_finished on O(1) kompleksinen.

remove_place tarkistaa, onko id:llä varustettu paikka olemassa, ja jos on, se poistetaan. Funktiota hidastaa place_vecista poistaminen, jota varten vektori täytyy käydä pahimmillaan kahdesti läpi ja parhaassakin tapauksessa kerran. Tämän kompleksisuus on siis O(n).

common_area_of_subareas metodia ei ole toteutettu.

_Toinen osa_

Graafi on toteutettu unordered_mapin, jossa avaimena on Coord ja sisältönä shared pointer noodiin, sillä näin päästään helposti ja keskimäärin nopeasti käsiksi kaikkeen oleelliseen. Tiet tallennetaan myös ways-nimiseen unordered_mapiin, jotta tien haku WayID:n perusteella olisi nopeaa.
Teitä ja noodeja varten on toteutettu omat structinsa, jotka sisältävät tarpeelliset tiedot. Node-structista mainittakoon, että siinä on erilaisia hakuja varten ylimääräisiä muuttujia, Dijkstran algoritmia varten määritelty vertailuoperaattori ja viereiset solmut on tallennettu nopeaan unordered_map adjs:iin.

Add_way metodi kokeilee lisätä tien ways:iin ja onnistuessaan jatkaa lisäämistä myös graafiin. Funktio on pahimmillaan O(n), mutta jos hajautus toimii suotuisasti, on se keskimäärin theta log(n).

all_ways iteroi waysin läpi ja lisää teitä vektoriin, joten se on lineaarinen teiden määrään nähden. Vastaavasti clear_ways iteroi sekä waysin että graphin läpi, joten sekin on lineaarinen mutta O(n + e) jossa n on risteyksien ja e teiden lukumäärä.

ways_from etsii graafista oikean noodin ja käy läpi sen viereiset solmut palauttaen niihin johtavien teiden id:t. Pahimmillaan kaikki tiet lähtevät tästä solmusta ja etsimisessä kestää kauan, joten tämä funktio on O(n). Keskimäärin tämä on kuitenkin vakioaikainen.

get_way_coords on käytetyn find-agoritmin johdosta pahimmillaan ajankäytöltään lineaarinen etsintäfunktion johdosta, mutta keskimäärin vakioaikainen.

Polkuja käsittelevät algoritmit route_any, route_least_crossings, route_shortest distance sekä route_with_cycle ovat kaikki ajankäytöltään O(n+e) sillä niissä joudutaan pahimmillaan käymään läpi jokainen graafin solmu ja tie. route_any ja route_least_crossings on toteutettu samalla breadth-first search algoritmilla, sillä se on riittävän tehokas ja toimii molempiin funktioihin. route_shortest distance on toteutettu Dijkstran algoritmilla, joka toimii hyvin painotettujen graafien lyhimmän polun etsimisessä ja route_with_cycle:ssa on käytetty depth-first searchia hieman mukaillusti, jotta kuljettu polku saadaan täyteen.

remove_way on keskimäärin logaritminen, mutta unordered_mapin johdosta voi huonolla tuurilla myös lineaarinen.
