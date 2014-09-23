@echo OFF

java -jar "C:\\Users\\igMoreira\\Desktop\\test.jar" fuzz discover http://localhost:8080 --common-words=words.txt > happyCase.txt

java -jar "C:\\Users\\igMoreira\\Desktop\\test.jar" > erroCaseDVWA1.txt

java -jar "C:\\Users\\igMoreira\\Desktop\\test.jar" fuzz > erroCaseDVWA2.txt

java -jar "C:\\Users\\igMoreira\\Desktop\\test.jar" fuzz discover > erroCaseDVWA3.txt

java -jar "C:\\Users\\igMoreira\\Desktop\\test.jar" fuzz discover http://localhost:8080 > erroCaseDVWA3.txt

java -jar "C:\\Users\\igMoreira\\Desktop\\test.jar" fuzz discover http://localhost:8080 --custom-auth=dvwa > erroCaseDVWA4.txt

java -jar "C:\\Users\\igMoreira\\Desktop\\test.jar" fuzz discover http://localhost:8080 --custom-auth=dvwa --common-words=words.txt > erroCaseDVWA5.txt

java -jar "C:\\Users\\igMoreira\\Desktop\\test.jar" fuzz discover http://localhost:8080 --custom-auth=bodgeit > erroCaseBODGEIT1.txt

java -jar "C:\\Users\\igMoreira\\Desktop\\test.jar" fuzz discover http://localhost:8080 --custom-auth=bodgeit --common-words=words.txt > erroCaseBODGEIT2.txt