# animal-shelter-bot
Animal Shelter Telegram Bot Sky.Pro project (JD-6 Team 5)

Animal Shelter Telegram Bot Java project is Sky.Pro educational project. This is Telegram bot for Animal Shelter 
customers, who would like to adopt animals from the shelter.  

To start application please use jar file from this repository. Last version is 0.5.1. Command line for starting jar 
file: 

java -jar -D"telegram.bot.token={TelegramBotToken}" -D"spring.datasource.username={database user name}" 
    -D"spring.datasource.password={database user password}" 
    -D"spring.datasource.url=jdbc:postgresql://{database server IP address}/{database name}"
    animal-shelter-telegram-bot-0.5.1.jar

You can also define environment variables in your operating system:

telegram.bot.token={TelegramBotToken} 
spring.datasource.username={database user name}
spring.datasource.password={database user password}
spring.datasource.url=jdbc:postgresql://{database server IP address}/{database name}

Command line to start application in this case will be 

java -jar animal-shelter-telegram-bot-0.5.1.jar

Sky.Pro Java Developer Course 6 Team 5 (JD-6 Team 5)
    Aleksandr Tsygulev (aleksandr.tsygulev@gmail.com)
    Nikolaj Zubovskij (nzubovskiy@gmail.com)
    Vasily Demin (vasily.demin@gmail.com)
