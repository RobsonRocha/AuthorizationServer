## AuthorizationServer

## Motivação

Projeto criado para prover serviços REST para o sistema [AccessClient] (https://github.com/RobsonRocha/AccessClient).
	
Utilizando o auxílio do framework [Spring 3] (https://spring.io/), o intuito foi mostrar a interação entre sistemas.

A ideia é que existe um sistema hipotético que tem arquivos e somente quem tem os perfis adequados pode acessá-los. E para se ter esses perfis, o usuário deverá solicitá-los via [AccessClient] (https://github.com/RobsonRocha/AccessClient). 

Como a proposta foi só mostrar o funcionamento das tecnologias, não foi criado nenhum banco de dados, ficando tudo em memória.
 
## Linguagem

A linguagem utilizada é Java.

## Serviço

Foram implementados os seguintes serviços REST:

* `http://endereco/AuthorizationServer/rest/permission/allpermissions`

Busca todas as permissões.

* `http://endereco/AuthorizationServer/rest/permission/allaccesspermissions`	

Busca todos os tipos de permissões.

* `http://endereco/AuthorizationServer/rest/permission/alluserpermissions/[login]`

Busca todas as permissões de um usuário, se o usuário for administrador, busca todas sem filtrar.
Onde login é o login do usuário logado no sistema.

* `http://endereco/AuthorizationServer/rest/permission/[id]`

Busca uma permissão específica. Onde id é o id da permissão.

* `http://endereco/AuthorizationServer/rest/createpermission`

Cria uma permissão.

* `http://endereco/AuthorizationServer/rest/updatepermission`

Atualiza uma permissão.

* `http://endereco/AuthorizationServer/rest/deletepermission`

Apaga uma permissão.

* `http://endereco/AuthorizationServer/rest/associatepermission`

Associa a permissão ao usuário.

* `http://endereco/AuthorizationServer/rest/desassociatepermission`

Desassocia a permissão do usuário.

* `http://endereco/AuthorizationServer/rest/user/login`

Faz o login do usuário.

* `http://endereco/AuthorizationServer/rest/user/createadmin`

Cria um usuário administrador, usado somente para teste.

* `http://endereco/AuthorizationServer/rest/user/[login]`

Busca um usuário específico. Onde login é o login do usuário a ser buscado.

* `http://endereco/AuthorizationServer/rest/allusers/[login]`

Busca todos os usuários, se o login passado por parâmetro for de um administrador, caso contrário, só busca o usuário logado no sistema.

* `http://endereco/AuthorizationServer/rest/createuser`

Cria um usuário.

* `http://endereco/AuthorizationServer/rest/user/updateuser`

Atualiza o usuário.

* `http://endereco/AuthorizationServer/rest/deleteuser`

Apaga um usuário.

* `http://endereco/AuthorizationServer/rest/changepassword`

Altera a senha de um usuário.

* `http://endereco/AuthorizationServer/rest/user/requestassociation/[login]`

Solicita um acesso a um ou mais perfis. Onde login é o login do usuário que requisitou o acesso.

* `http://endereco/AuthorizationServer/rest/user/deleterequestassociation/[login]`

Apaga uma solicitação.

* `http://endereco/AuthorizationServer/rest/user/requestdesassociation/[login]`

Requisita uma desassociação.

* `http://endereco/AuthorizationServer/rest/user/deleterequestdesassociation/[login]`

Apaga uma requisição de desassociação.

* `http://endereco/AuthorizationServer/rest/allrequestdassociations`

Busca todas as solicitações associações.

* `http://endereco/AuthorizationServer/rest/allrequestddesassociations`

Busca todas as solicitações de desassociações.

* `http://endereco/AuthorizationServer/rest/requestdassociation/[login]`

Busca solicitações de associações específicas do usuário passado como parâmetro.

* `http://endereco/AuthorizationServer/rest/requestddesassociation/[login]`

Busca solicitações de desassociações específicas do usuário passado como parâmetro.


## Compilação

Para facilitar a importação de bibliotecas e a compilação dos arquivos em um único pacote, foi utilizado Maven.
Para compilar gerando o pacote basta executar o comando abaixo na linha de comando.

```mvn -DskipTests compile package```

Na pasta target serão gerados vários arquivos, mas o pacote principal é gerado com o nome `AuthorizationServer-1.0.0-BUILD-SNAPSHOT.war`


##Testes

Para os testes foram utilizadas as bibliotecas TestNG.
Para executar os testes basta escrever na linha de comando abaixo com o sistema no ar.
Inclusive os testes deixam o sistema populado com exemplos de perfis, usuários, associações e requisições.

 ```mvn test```


## Execução

O container utilizado para execução do projeto foi o [Tomcat 8.0](http://tomcat.apache.org/download-80.cgi) com a configuração padrão.
