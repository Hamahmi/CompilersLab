grammar task10;

start: s EOF {System.out.println($s.val);};

s
	returns[int val]: e {$val = ($e.val)? 1 : 0;};

e
	returns[boolean val]:
	E1 = e '+' t {$val = $E1.val || $t.val;}
	| t {$val = $t.val;};
t
	returns[boolean val]:
	T1 = t '&' f {$val = $T1.val && $f.val;}
	| f {$val = $f.val;};
f
	returns[boolean val]:
	'(' e ')' {$val = $e.val;}
	| id {$val = $id.val;}
	| '!' '(' e ')' {$val = ! $e.val;}
	| '!' id {$val = ! $id.val;};
id
	returns[boolean val]: '0' {$val = false;} | '1' {$val = true;};

WS: [ \r\n\t]+ -> skip;