# Introduction
Estat Japan is a tool to retrieve Japan government's public stats and to convert them into csv. Those csv's can then easily be plugged into machine learning algorithms.

# How to use
1.	You will need an e-Stat API ID, one can be retrieved for free at : https://www.e-stat.go.jp/mypage/user/preregister
2.  You will need to setup your API ID into a application.properties which should be located under the config directory, it should contain the following settings
	output_path=The localtion of where the stats will be generated for example G:\\estat\\
	log_path=The path were diagnostics will put written : for example G:\\estat\\logs
	app_id=The API ID you got from the e-stat website
	use_cache_data=true // false redownload everything, true downloads only the content that have changed or contents that have not been retrieved yet
	mindate=2015-01-01 //the minimum date from which you want to retrieve data

With the above parameters you can expect a few TeraBytes of data downloaded for about a week due to the throttling parameters that have been set inside the application.

# Stats settings

The set of stats that can be retrieved can be modified by changing the :
en_stats.csv for english available stats
jp_stats/csv for japanese available stats
Note that there are currently much more available stats in japanese than english.

You can get a description of the different stats at the following URL :
https://www.e-stat.go.jp/stat-search?page=1

# License
Stat japan is Open Source software released under the [Apache 2.0 license](https://www.apache.org/licenses/LICENSE-2.0). 

# Build
The project is using maven to be build
mvn install
The jar file will be located inside the target directory
