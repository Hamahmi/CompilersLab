grammar task8;

@parser::header {
 import java.lang.Math;
}

start: s EOF;

///////////////////

s
	returns[double val]:
	l '.' r {$val = $l.val + $r.val;
    System.out.println($val);};
l
	returns[double n, double val]:
	b L1 = l { $n = $L1.n + 1; $val = $b.val * Math.pow(2, $n) + $L1.val;}
	| b {$n = 0.0; $val = $b.val;};
r
	returns[double val]:
	b R1 = r {$val = ($R1.val * 0.5) + ($b.val * 0.5);}
	| b {$val = $b.val * 0.5;};
b
	returns[double val]: '0' {$val = 0.0;} | '1' {$val = 1.0;};

///////////////////

WS: [ \r\n\t]+ -> skip;