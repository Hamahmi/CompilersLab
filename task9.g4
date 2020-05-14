grammar task9;

start: s EOF {System.out.println($s.val);};

///////////////////

s
	returns[String val]:
	'nil' {$val = "()";}
	| l {$val = "(" +  $l.val + ")";};

l
	returns[String val]:
	'(' D '.' L1 = l ')' {$val =  $D.text + ", " + $L1.val ;}
	| '(' D '.' 'nil' ')' {$val =  $D.text;};

D: [0-9];

///////////////////

WS: [ \r\n\t]+ -> skip;