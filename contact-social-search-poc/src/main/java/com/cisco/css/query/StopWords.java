package com.cisco.css.query;

public class StopWords {

	private static String stopWords = "a,able,about,above,abroad,according,accordingly,across," +
			"actually,adj,after,afterwards,again,against,ago,ahead,ain't,all,allow,allows,almost," +
			"alone,along,alongside,already,also,although,always,am,amid,amidst,among,amongst,an,and," +
			"apart,appear,are,aren't,around,as,a's,aside,at,be,became,become,been,before,beforehand," +
			"begin,behind,being,believe,below,beside,besides,best,better,between,beyond,both,brief,but," +
			"by,c,came,can,cannot,cant,can't,caption,cause,causes,certain,certainly,changes,clearly,c'mon," +
			"co,co.,com,come,comes,concerning,consequently,consider,considering,contain,containing,contains," +
			"corresponding,could,couldn't,course,c's,currently,d,dare,daren't,definitely,described,despite," +
			"did,didn't,different,directly,do,does,doesn't,doing,done,don't,down,downwards,during,e,each,edu," +
			"eg,eight,eighty,either,else,elsewhere,end,ending,enough,entirely,especially,et,etc,even,ever,evermore," +
			"every,ex,exactly,except,f,fairly,far,farther,few,fewer,fifth,first,five,followed,following,follows,for," +
			"forever,former,formerly,forth,forward,found,four,from,further,furthermore,g,get,gets,getting,given,gives," +
			"go,goes,going,gone,got,gotten,h,had,hadn't,half,happens,hardly,has,hasn't,have,haven't,having,he,he'd," +
			"he'll,hello,help,hence,her,here,hereafter,hereby,herein,here's,hereupon,hers,herself,he's,hi,him,himself," +
			"his,hither,hopefully,how,howbeit,however,hundred,i,i'd,ie,if,ignored,i'll,i'm,immediate,in,inasmuch,inc," +
			"inc.,indeed,indicate,indicated,indicates,inner,inside,insofar,instead,into,inward,is,isn't,it,it'd,it'll,its," +
			"it's,itself,i've,j,just,k,l,last,lately,later,latter,latterly,least,less,lest,let,let's,little,low,lower,m," +
			"mainly,many,may,maybe,mayn't,me,meantime,meanwhile,merely,might,mightn't,mine,more,moreover,most,mostly,mr,mrs," +
			"much,must,mustn't,my,myself,n,namely,nd,nearly,neither,never,neverf,neverless,nevertheless,nonetheless,noone,nor," +
			"normally,not,o,obviously,of,off,often,oh,okay,old,on,once,one,ones,one's,only,onto,opposite,or,other,others," +
			"otherwise,ought,oughtn't,our,ours,ourselves,p,particular,particularly,per,perhaps,possible,presumably,probably," +
			"provides,q,qv,r,rather,rd,re,really,reasonably,recent,recently,relatively,respectively,s,same,secondly,seem," +
			"seemed,seeming,seems,seen,self,selves,sensible,serious,seriously,several,shall,shan't,she,she'd,she'll,she's," +
			"should,shouldn't,since,so,some,soon,specify,specifying,still,such,t,th,than,that,that'll,thats,that's,that've," +
			"the,their,theirs,them,themselves,then,thence,there,thereafter,thereby,there'd,therefore,therein,there'll," +
			"there're,theres,there's,thereupon,there've,these,they,they'd,they'll,they're,they've,third,thirty,this,thorough," +
			"thoroughly,those,though,through,throughout,thru,thus,to,together,too,toward,towards,truly,trying,t's,u,un,under," +
			"underneath,undoing,unfortunately,unless,unlike,unlikely,until,unto,up,upon,upwards,us,useful,usually,v,various," +
			"versus,very,via,viz,vs,w,was,wasn't,we,we'd,welcome,well,we'll,went,were,we're,weren't,we've,what,whatever,what'll," +
			"what's,what've,when,whence,whenever,where,whereafter,whereas,whereby,wherein,where's,whereupon,wherever,whether,which," +
			"whichever,while,whilst,whither,who,who'd,whoever,whole,who'll,whom,whomever,who's,whose,why,will,with,within,without," +
			"wonder,won't,would,wouldn't,x,y,yet,you,you'd,you'll,your,you're,yours,yourself,yourselves,you've,z";
	
	
	
	public static String stop(String src) {
		String[] wl = src.split(" ");
		for(String word : wl) {
			if(stopWords.contains(word)) {
				src.replace(word, "");
			}
		}
		return src;
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
	}

}
