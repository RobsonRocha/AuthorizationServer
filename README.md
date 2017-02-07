## AuthorizationServer

## Motiva��o

Projeto criado para prover servi�os REST para o sistema [AccessClient] (https://github.com/RobsonRocha/AccessClient).
	
Utilizando o aux�lio do framework [Spring 3] (https://spring.io/), o intuito foi mostrar a intera��o entre sistemas.

A ideia � que existe um sistema hipot�tico que tem arquivos e somente quem tem os perfis adequados pode acess�-los. E para se ter esses perfis, o usu�rio dever� solicit�-los via [AccessClient] (https://github.com/RobsonRocha/AccessClient). 

Como a proposta foi s� mostrar o funcionamento das tecnologias, n�o foi criado nenhum banco de dados, ficando tudo em mem�ria.
 
## Linguagem

A linguagem utilizada � Java.

## Servi�o

Foram implementados os seguintes servi�os REST:

* `http://endereco/AuthorizationServer/rest/permission/allpermissions`

Busca todas as permiss�es.

* `http://endereco/AuthorizationServer/rest/permission/allaccesspermissions`	

Busca todos os tipos de permiss�es.

* `http://endereco/AuthorizationServer/rest/permission/alluserpermissions/[login]`

Busca todas as permiss�es de um usu�rio, se o usu�rio for administrador, busca todas sem filtrar.
Onde login � o login do usu�rio logado no sistema.

* `http://endereco/AuthorizationServer/rest/permission/[id]`

Busca uma permiss�o espec�fica. Onde id � o id da permiss�o.

* `http://endereco/AuthorizationServer/rest/createpermission`

Cria uma permiss�o.

* `http://endereco/AuthorizationServer/rest/updatepermission`

Atualiza uma permiss�o.

* `http://endereco/AuthorizationServer/rest/deletepermission`

Apaga uma permiss�o.

* `http://endereco/AuthorizationServer/rest/associatepermission`

Associa a permiss�o ao usu�rio.

* `http://endereco/AuthorizationServer/rest/desassociatepermission`

Desassocia a permiss�o do usu�rio.

* `http://endereco/AuthorizationServer/rest/user/login`

Faz o login do usu�rio.

* `http://endereco/AuthorizationServer/rest/user/createadmin`

Cria um usu�rio administrador, usado somente para teste.

* `http://endereco/AuthorizationServer/rest/user/[login]`

Busca um usu�rio espec�fico. Onde login � o login do usu�rio a ser buscado.

* `http://endereco/AuthorizationServer/rest/allusers/[login]`

Busca todos os usu�rios, se o login passado por par�metro for de um administrador, caso contr�rio, s� busca o usu�rio logado no sistema.

* `http://endereco/AuthorizationServer/rest/createuser`

Cria um usu�rio.

* `http://endereco/AuthorizationServer/rest/user/updateuser`

Atualiza o usu�rio.

* `http://endereco/AuthorizationServer/rest/deleteuser`

Apaga um usu�rio.

* `http://endereco/AuthorizationServer/rest/changepassword`

Altera a senha de um usu�rio.

* `http://endereco/AuthorizationServer/rest/user/requestassociation/[login]`

Solicita um acesso a um ou mais perfis. Onde login � o login do usu�rio que requisitou o acesso.

* `http://endereco/AuthorizationServer/rest/user/deleterequestassociation/[login]`

Apaga uma solicita��o.

* `http://endereco/AuthorizationServer/rest/user/requestdesassociation/[login]`

Requisita uma desassocia��o.

* `http://endereco/AuthorizationServer/rest/user/deleterequestdesassociation/[login]`

Apaga uma requisi��o de desassocia��o.

* `http://endereco/AuthorizationServer/rest/allrequestdassociations`

Busca todas as solicita��es associa��es.

* `http://endereco/AuthorizationServer/rest/allrequestddesassociations`

Busca todas as solicita��es de desassocia��es.

* `http://endereco/AuthorizationServer/rest/requestdassociation/[login]`

Busca solicita��es de associa��es espec�ficas do usu�rio passado como par�metro.

* `http://endereco/AuthorizationServer/rest/requestddesassociation/[login]`

Busca solicita��es de desassocia��es espec�ficas do usu�rio passado como par�metro.


## Compila��o

Para facilitar a importa��o de bibliotecas e a compila��o dos arquivos em um �nico pacote, foi utilizado Maven.
Para compilar gerando o pacote basta executar o comando abaixo na linha de comando.

```mvn -DskipTests compile package```

Na pasta target ser�o gerados v�rios arquivos, mas o pacote principal � gerado com o nome `AuthorizationServer-1.0.0-BUILD-SNAPSHOT.war`


##Testes

Para os testes foram utilizadas as bibliotecas TestNG.
Para executar os testes basta escrever na linha de comando abaixo com o sistema no ar.
Inclusive os testes deixam o sistema populado com exemplos de perfis, usu�rios, associa��es e requisi��es.

 ```mvn test```


## Execu��o

O container utilizado para execu��o do projeto foi o [Tomcat 8.0](http://tomcat.apache.org/download-80.cgi) com a configura��o padr�o.
