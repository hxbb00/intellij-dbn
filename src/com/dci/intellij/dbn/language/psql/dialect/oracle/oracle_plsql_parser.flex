package com.dci.intellij.dbn.language.psql.dialect.oracle;

import com.dci.intellij.dbn.language.common.TokenTypeBundle;
import com.intellij.lexer.FlexLexer;
import com.intellij.psi.tree.IElementType;

%%

%class OraclePLSQLParserFlexLexer
%implements FlexLexer
%public
%final
%unicode
%ignorecase
%function advance
%type IElementType
%eof{ return;
%eof}

%{
    private TokenTypeBundle tt;
    public OraclePLSQLParserFlexLexer(TokenTypeBundle tt) {
        this.tt = tt;
    }
%}

WHITE_SPACE= {white_space_char}|{line_terminator}
line_terminator = \r|\n|\r\n
input_character = [^\r\n]
white_space = [ \t\f]
white_space_char= [ \n\r\t\f]
ws  = {WHITE_SPACE}+
wso = {WHITE_SPACE}*

comment_tail =([^"*"]*("*"+[^"*""/"])?)*("*"+"/")?
BLOCK_COMMENT=("/*"[^]{comment_tail})|"/*"
LINE_COMMENT = "--" {input_character}*
REM_LINE_COMMENT = "rem"({white_space}+{input_character}*|{line_terminator})

IDENTIFIER = [:jletter:] ([:jletterdigit:]|"#")*
QUOTED_IDENTIFIER = "\""[^\"]*"\""?

string_simple_quoted      = "'"([^\']|"''")*"'"?
string_alternative_quoted = "q'["[^\[\]]*"]'"? | "q'("[^\(\)]*")'"? | "q'{"[^\{\}]*"}'"? | "q'!"[^\!]*"!'"? | "q'<"[^\<\>]*">'"? | "q'|"[^|]*"|'"?
STRING = "n"?({string_alternative_quoted}|{string_simple_quoted})

sign = "+"|"-"
digit = [0-9]
INTEGER = {digit}+("e"{sign}?{digit}+)?
NUMBER = {INTEGER}?"."{digit}+(("e"{sign}?{digit}+)|(("f"|"d"){ws}))?

VARIABLE = ":"{INTEGER}
SQLP_VARIABLE = "&""&"?{IDENTIFIER}

%state PLSQL, WRAPPED
%%

<WRAPPED> {
    .*               { return tt.getSharedTokenTypes().getLineComment(); }
    .               { return tt.getSharedTokenTypes().getLineComment(); }
}


{WHITE_SPACE}+   { return tt.getSharedTokenTypes().getWhiteSpace(); }

{BLOCK_COMMENT}      { return tt.getSharedTokenTypes().getBlockComment(); }
{LINE_COMMENT}       { return tt.getSharedTokenTypes().getLineComment(); }
{REM_LINE_COMMENT}   { return tt.getSharedTokenTypes().getLineComment(); }

"wrapped"            { yybegin(WRAPPED); return tt.getTokenType("KW_WRAPPED");}

{VARIABLE}          {return tt.getSharedTokenTypes().getVariable(); }
{SQLP_VARIABLE}     {return tt.getSharedTokenTypes().getVariable(); }


{INTEGER}     { return tt.getSharedTokenTypes().getInteger(); }
{NUMBER}      { return tt.getSharedTokenTypes().getNumber(); }
{STRING}      { return tt.getSharedTokenTypes().getString(); }

"("{wso}"+"{wso}")"  {return tt.getTokenType("CT_OUTER_JOIN");}

"="{wso}"=" {return tt.getOperatorTokenType(0);}
"|"{wso}"|" {return tt.getOperatorTokenType(1);}
"<"{wso}"=" {return tt.getOperatorTokenType(2);}
">"{wso}"=" {return tt.getOperatorTokenType(3);}
"<"{wso}">" {return tt.getOperatorTokenType(4);}
"!"{wso}"=" {return tt.getOperatorTokenType(5);}
":"{wso}"=" {return tt.getOperatorTokenType(6);}
"="{wso}">" {return tt.getOperatorTokenType(7);}
".."        {return tt.getOperatorTokenType(8);}
"::"        {return tt.getOperatorTokenType(9);}



"@" {return tt.getCharacterTokenType(0);}
":" {return tt.getCharacterTokenType(1);}
"," {return tt.getCharacterTokenType(2);}
"." {return tt.getCharacterTokenType(3);}
"=" {return tt.getCharacterTokenType(4);}
"!" {return tt.getCharacterTokenType(5);}
">" {return tt.getCharacterTokenType(6);}
"#" {return tt.getCharacterTokenType(7);}
"[" {return tt.getCharacterTokenType(8);}
"{" {return tt.getCharacterTokenType(9);}
"(" {return tt.getCharacterTokenType(10);}
"<" {return tt.getCharacterTokenType(11);}
"-" {return tt.getCharacterTokenType(12);}
"%" {return tt.getCharacterTokenType(13);}
"+" {return tt.getCharacterTokenType(14);}
"]" {return tt.getCharacterTokenType(15);}
"}" {return tt.getCharacterTokenType(16);}
")" {return tt.getCharacterTokenType(17);}
";" {return tt.getCharacterTokenType(18);}
"/" {return tt.getCharacterTokenType(19);}
"*" {return tt.getCharacterTokenType(20);}
"|" {return tt.getCharacterTokenType(21);}




"varchar2" {return tt.getDataTypeTokenType(0);}
"bfile" {return tt.getDataTypeTokenType(1);}
"binary_double" {return tt.getDataTypeTokenType(2);}
"binary_float" {return tt.getDataTypeTokenType(3);}
"binary_integer" {return tt.getDataTypeTokenType(4);}
"blob" {return tt.getDataTypeTokenType(5);}
"boolean" {return tt.getDataTypeTokenType(6);}
"byte" {return tt.getDataTypeTokenType(7);}
"char" {return tt.getDataTypeTokenType(8);}
"character" {return tt.getDataTypeTokenType(9);}
"character"{ws}"varying" {return tt.getDataTypeTokenType(10);}
"clob" {return tt.getDataTypeTokenType(11);}
"date" {return tt.getDataTypeTokenType(12);}
"decimal" {return tt.getDataTypeTokenType(13);}
"double"{ws}"precision" {return tt.getDataTypeTokenType(14);}
"float" {return tt.getDataTypeTokenType(15);}
"int" {return tt.getDataTypeTokenType(16);}
"integer" {return tt.getDataTypeTokenType(17);}
"long" {return tt.getDataTypeTokenType(18);}
"long"{ws}"raw" {return tt.getDataTypeTokenType(19);}
"long"{ws}"varchar" {return tt.getDataTypeTokenType(20);}
"national"{ws}"char" {return tt.getDataTypeTokenType(21);}
"national"{ws}"char"{ws}"varying" {return tt.getDataTypeTokenType(22);}
"national"{ws}"character" {return tt.getDataTypeTokenType(23);}
"national"{ws}"character"{ws}"varying" {return tt.getDataTypeTokenType(24);}
"nchar" {return tt.getDataTypeTokenType(25);}
"nchar"{ws}"varying" {return tt.getDataTypeTokenType(26);}
"nclob" {return tt.getDataTypeTokenType(27);}
"number" {return tt.getDataTypeTokenType(28);}
"numeric" {return tt.getDataTypeTokenType(29);}
"nvarchar2" {return tt.getDataTypeTokenType(30);}
"pls_integer" {return tt.getDataTypeTokenType(31);}
"raw" {return tt.getDataTypeTokenType(32);}
"real" {return tt.getDataTypeTokenType(33);}
"rowid" {return tt.getDataTypeTokenType(34);}
"smallint" {return tt.getDataTypeTokenType(35);}
"string" {return tt.getDataTypeTokenType(36);}
"timestamp" {return tt.getDataTypeTokenType(37);}
"urowid" {return tt.getDataTypeTokenType(38);}
"varchar" {return tt.getDataTypeTokenType(39);}
"with"{ws}"local"{ws}"time"{ws}"zone" {return tt.getDataTypeTokenType(40);}
"with"{ws}"time"{ws}"zone" {return tt.getDataTypeTokenType(41);}





"a set" {return tt.getKeywordTokenType(0);}
"absent" {return tt.getKeywordTokenType(1);}
"accessible" {return tt.getKeywordTokenType(2);}
"after" {return tt.getKeywordTokenType(3);}
"agent" {return tt.getKeywordTokenType(4);}
"all" {return tt.getKeywordTokenType(5);}
"alter" {return tt.getKeywordTokenType(6);}
"analyze" {return tt.getKeywordTokenType(7);}
"and" {return tt.getKeywordTokenType(8);}
"any" {return tt.getKeywordTokenType(9);}
"apply" {return tt.getKeywordTokenType(10);}
"array" {return tt.getKeywordTokenType(11);}
"as" {return tt.getKeywordTokenType(12);}
"asc" {return tt.getKeywordTokenType(13);}
"associate" {return tt.getKeywordTokenType(14);}
"at" {return tt.getKeywordTokenType(15);}
"audit" {return tt.getKeywordTokenType(16);}
"authid" {return tt.getKeywordTokenType(17);}
"automatic" {return tt.getKeywordTokenType(18);}
"autonomous_transaction" {return tt.getKeywordTokenType(19);}
"before" {return tt.getKeywordTokenType(20);}
"begin" {return tt.getKeywordTokenType(21);}
"between" {return tt.getKeywordTokenType(22);}
"block" {return tt.getKeywordTokenType(23);}
"body" {return tt.getKeywordTokenType(24);}
"both" {return tt.getKeywordTokenType(25);}
"bulk" {return tt.getKeywordTokenType(26);}
"bulk_exceptions" {return tt.getKeywordTokenType(27);}
"bulk_rowcount" {return tt.getKeywordTokenType(28);}
"by" {return tt.getKeywordTokenType(29);}
"c" {return tt.getKeywordTokenType(30);}
"call" {return tt.getKeywordTokenType(31);}
"canonical" {return tt.getKeywordTokenType(32);}
"case" {return tt.getKeywordTokenType(33);}
"char_base" {return tt.getKeywordTokenType(34);}
"char_cs" {return tt.getKeywordTokenType(35);}
"charsetform" {return tt.getKeywordTokenType(36);}
"charsetid" {return tt.getKeywordTokenType(37);}
"check" {return tt.getKeywordTokenType(38);}
"chisq_df" {return tt.getKeywordTokenType(39);}
"chisq_obs" {return tt.getKeywordTokenType(40);}
"chisq_sig" {return tt.getKeywordTokenType(41);}
"close" {return tt.getKeywordTokenType(42);}
"cluster" {return tt.getKeywordTokenType(43);}
"coalesce" {return tt.getKeywordTokenType(44);}
"coefficient" {return tt.getKeywordTokenType(45);}
"cohens_k" {return tt.getKeywordTokenType(46);}
"collation" {return tt.getKeywordTokenType(47);}
"collect" {return tt.getKeywordTokenType(48);}
"columns" {return tt.getKeywordTokenType(49);}
"comment" {return tt.getKeywordTokenType(50);}
"commit" {return tt.getKeywordTokenType(51);}
"committed" {return tt.getKeywordTokenType(52);}
"compatibility" {return tt.getKeywordTokenType(53);}
"compound" {return tt.getKeywordTokenType(54);}
"compress" {return tt.getKeywordTokenType(55);}
"conditional" {return tt.getKeywordTokenType(56);}
"connect" {return tt.getKeywordTokenType(57);}
"constant" {return tt.getKeywordTokenType(58);}
"constraint" {return tt.getKeywordTokenType(59);}
"constructor" {return tt.getKeywordTokenType(60);}
"cont_coefficient" {return tt.getKeywordTokenType(61);}
"content" {return tt.getKeywordTokenType(62);}
"context" {return tt.getKeywordTokenType(63);}
"count" {return tt.getKeywordTokenType(64);}
"cramers_v" {return tt.getKeywordTokenType(65);}
"create" {return tt.getKeywordTokenType(66);}
"cross" {return tt.getKeywordTokenType(67);}
"cube" {return tt.getKeywordTokenType(68);}
"current" {return tt.getKeywordTokenType(69);}
"current_user" {return tt.getKeywordTokenType(70);}
"currval" {return tt.getKeywordTokenType(71);}
"cursor" {return tt.getKeywordTokenType(72);}
"database" {return tt.getKeywordTokenType(73);}
"day" {return tt.getKeywordTokenType(74);}
"db_role_change" {return tt.getKeywordTokenType(75);}
"ddl" {return tt.getKeywordTokenType(76);}
"declare" {return tt.getKeywordTokenType(77);}
"decrement" {return tt.getKeywordTokenType(78);}
"default" {return tt.getKeywordTokenType(79);}
"defaults" {return tt.getKeywordTokenType(80);}
"definer" {return tt.getKeywordTokenType(81);}
"delete" {return tt.getKeywordTokenType(82);}
"deleting" {return tt.getKeywordTokenType(83);}
"dense_rank" {return tt.getKeywordTokenType(84);}
"desc" {return tt.getKeywordTokenType(85);}
"deterministic" {return tt.getKeywordTokenType(86);}
"df" {return tt.getKeywordTokenType(87);}
"df_between" {return tt.getKeywordTokenType(88);}
"df_den" {return tt.getKeywordTokenType(89);}
"df_num" {return tt.getKeywordTokenType(90);}
"df_within" {return tt.getKeywordTokenType(91);}
"dimension" {return tt.getKeywordTokenType(92);}
"disable" {return tt.getKeywordTokenType(93);}
"disassociate" {return tt.getKeywordTokenType(94);}
"distinct" {return tt.getKeywordTokenType(95);}
"do" {return tt.getKeywordTokenType(96);}
"document" {return tt.getKeywordTokenType(97);}
"drop" {return tt.getKeywordTokenType(98);}
"dump" {return tt.getKeywordTokenType(99);}
"duration" {return tt.getKeywordTokenType(100);}
"each" {return tt.getKeywordTokenType(101);}
"editionable" {return tt.getKeywordTokenType(102);}
"else" {return tt.getKeywordTokenType(103);}
"elsif" {return tt.getKeywordTokenType(104);}
"empty" {return tt.getKeywordTokenType(105);}
"enable" {return tt.getKeywordTokenType(106);}
"encoding" {return tt.getKeywordTokenType(107);}
"end" {return tt.getKeywordTokenType(108);}
"entityescaping" {return tt.getKeywordTokenType(109);}
"equals_path" {return tt.getKeywordTokenType(110);}
"error" {return tt.getKeywordTokenType(111);}
"error_code" {return tt.getKeywordTokenType(112);}
"error_index" {return tt.getKeywordTokenType(113);}
"errors" {return tt.getKeywordTokenType(114);}
"escape" {return tt.getKeywordTokenType(115);}
"evalname" {return tt.getKeywordTokenType(116);}
"exact_prob" {return tt.getKeywordTokenType(117);}
"except" {return tt.getKeywordTokenType(118);}
"exception" {return tt.getKeywordTokenType(119);}
"exception_init" {return tt.getKeywordTokenType(120);}
"exceptions" {return tt.getKeywordTokenType(121);}
"exclude" {return tt.getKeywordTokenType(122);}
"exclusive" {return tt.getKeywordTokenType(123);}
"execute" {return tt.getKeywordTokenType(124);}
"exists" {return tt.getKeywordTokenType(125);}
"exit" {return tt.getKeywordTokenType(126);}
"extend" {return tt.getKeywordTokenType(127);}
"extends" {return tt.getKeywordTokenType(128);}
"external" {return tt.getKeywordTokenType(129);}
"f_ratio" {return tt.getKeywordTokenType(130);}
"fetch" {return tt.getKeywordTokenType(131);}
"final" {return tt.getKeywordTokenType(132);}
"first" {return tt.getKeywordTokenType(133);}
"following" {return tt.getKeywordTokenType(134);}
"follows" {return tt.getKeywordTokenType(135);}
"for" {return tt.getKeywordTokenType(136);}
"forall" {return tt.getKeywordTokenType(137);}
"force" {return tt.getKeywordTokenType(138);}
"found" {return tt.getKeywordTokenType(139);}
"from" {return tt.getKeywordTokenType(140);}
"format" {return tt.getKeywordTokenType(141);}
"full" {return tt.getKeywordTokenType(142);}
"function" {return tt.getKeywordTokenType(143);}
"goto" {return tt.getKeywordTokenType(144);}
"grant" {return tt.getKeywordTokenType(145);}
"group" {return tt.getKeywordTokenType(146);}
"having" {return tt.getKeywordTokenType(147);}
"heap" {return tt.getKeywordTokenType(148);}
"hide" {return tt.getKeywordTokenType(149);}
"hour" {return tt.getKeywordTokenType(150);}
"if" {return tt.getKeywordTokenType(151);}
"ignore" {return tt.getKeywordTokenType(152);}
"immediate" {return tt.getKeywordTokenType(153);}
"in" {return tt.getKeywordTokenType(154);}
"in"{ws}"out" {return tt.getKeywordTokenType(155);}
"include" {return tt.getKeywordTokenType(156);}
"increment" {return tt.getKeywordTokenType(157);}
"indent" {return tt.getKeywordTokenType(158);}
"index" {return tt.getKeywordTokenType(159);}
"indicator" {return tt.getKeywordTokenType(160);}
"indices" {return tt.getKeywordTokenType(161);}
"infinite" {return tt.getKeywordTokenType(162);}
"inline" {return tt.getKeywordTokenType(163);}
"inner" {return tt.getKeywordTokenType(164);}
"insert" {return tt.getKeywordTokenType(165);}
"inserting" {return tt.getKeywordTokenType(166);}
"instantiable" {return tt.getKeywordTokenType(167);}
"instead" {return tt.getKeywordTokenType(168);}
"interface" {return tt.getKeywordTokenType(169);}
"intersect" {return tt.getKeywordTokenType(170);}
"interval" {return tt.getKeywordTokenType(171);}
"into" {return tt.getKeywordTokenType(172);}
"is" {return tt.getKeywordTokenType(173);}
"isolation" {return tt.getKeywordTokenType(174);}
"isopen" {return tt.getKeywordTokenType(175);}
"iterate" {return tt.getKeywordTokenType(176);}
"java" {return tt.getKeywordTokenType(177);}
"join" {return tt.getKeywordTokenType(178);}
"json" {return tt.getKeywordTokenType(179);}
"keep" {return tt.getKeywordTokenType(180);}
"key" {return tt.getKeywordTokenType(181);}
"keys" {return tt.getKeywordTokenType(182);}
"language" {return tt.getKeywordTokenType(183);}
"last" {return tt.getKeywordTokenType(184);}
"leading" {return tt.getKeywordTokenType(185);}
"left" {return tt.getKeywordTokenType(186);}
"level" {return tt.getKeywordTokenType(187);}
"library" {return tt.getKeywordTokenType(188);}
"like" {return tt.getKeywordTokenType(189);}
"like2" {return tt.getKeywordTokenType(190);}
"like4" {return tt.getKeywordTokenType(191);}
"likec" {return tt.getKeywordTokenType(192);}
"limit" {return tt.getKeywordTokenType(193);}
"limited" {return tt.getKeywordTokenType(194);}
"local" {return tt.getKeywordTokenType(195);}
"lock" {return tt.getKeywordTokenType(196);}
"log" {return tt.getKeywordTokenType(197);}
"logoff" {return tt.getKeywordTokenType(198);}
"logon" {return tt.getKeywordTokenType(199);}
"loop" {return tt.getKeywordTokenType(200);}
"main" {return tt.getKeywordTokenType(201);}
"map" {return tt.getKeywordTokenType(202);}
"matched" {return tt.getKeywordTokenType(203);}
"maxlen" {return tt.getKeywordTokenType(204);}
"maxvalue" {return tt.getKeywordTokenType(205);}
"mean_squares_between" {return tt.getKeywordTokenType(206);}
"mean_squares_within" {return tt.getKeywordTokenType(207);}
"measures" {return tt.getKeywordTokenType(208);}
"member" {return tt.getKeywordTokenType(209);}
"merge" {return tt.getKeywordTokenType(210);}
"minus" {return tt.getKeywordTokenType(211);}
"minute" {return tt.getKeywordTokenType(212);}
"minvalue" {return tt.getKeywordTokenType(213);}
"mlslabel" {return tt.getKeywordTokenType(214);}
"mode" {return tt.getKeywordTokenType(215);}
"model" {return tt.getKeywordTokenType(216);}
"month" {return tt.getKeywordTokenType(217);}
"multiset" {return tt.getKeywordTokenType(218);}
"name" {return tt.getKeywordTokenType(219);}
"nan" {return tt.getKeywordTokenType(220);}
"natural" {return tt.getKeywordTokenType(221);}
"naturaln" {return tt.getKeywordTokenType(222);}
"nav" {return tt.getKeywordTokenType(223);}
"nchar_cs" {return tt.getKeywordTokenType(224);}
"nested" {return tt.getKeywordTokenType(225);}
"new" {return tt.getKeywordTokenType(226);}
"next" {return tt.getKeywordTokenType(227);}
"nextval" {return tt.getKeywordTokenType(228);}
"no" {return tt.getKeywordTokenType(229);}
"noaudit" {return tt.getKeywordTokenType(230);}
"nocopy" {return tt.getKeywordTokenType(231);}
"nocycle" {return tt.getKeywordTokenType(232);}
"noentityescaping" {return tt.getKeywordTokenType(233);}
"noneditionable" {return tt.getKeywordTokenType(234);}
"noschemacheck" {return tt.getKeywordTokenType(235);}
"not" {return tt.getKeywordTokenType(236);}
"notfound" {return tt.getKeywordTokenType(237);}
"nowait" {return tt.getKeywordTokenType(238);}
"null" {return tt.getKeywordTokenType(239);}
"nulls" {return tt.getKeywordTokenType(240);}
"number_base" {return tt.getKeywordTokenType(241);}
"object" {return tt.getKeywordTokenType(242);}
"ocirowid" {return tt.getKeywordTokenType(243);}
"of" {return tt.getKeywordTokenType(244);}
"offset" {return tt.getKeywordTokenType(245);}
"oid" {return tt.getKeywordTokenType(246);}
"old" {return tt.getKeywordTokenType(247);}
"on" {return tt.getKeywordTokenType(248);}
"one_sided_prob_or_less" {return tt.getKeywordTokenType(249);}
"one_sided_prob_or_more" {return tt.getKeywordTokenType(250);}
"one_sided_sig" {return tt.getKeywordTokenType(251);}
"only" {return tt.getKeywordTokenType(252);}
"opaque" {return tt.getKeywordTokenType(253);}
"open" {return tt.getKeywordTokenType(254);}
"operator" {return tt.getKeywordTokenType(255);}
"option" {return tt.getKeywordTokenType(256);}
"or" {return tt.getKeywordTokenType(257);}
"order" {return tt.getKeywordTokenType(258);}
"ordinality" {return tt.getKeywordTokenType(259);}
"organization" {return tt.getKeywordTokenType(260);}
"others" {return tt.getKeywordTokenType(261);}
"out" {return tt.getKeywordTokenType(262);}
"outer" {return tt.getKeywordTokenType(263);}
"over" {return tt.getKeywordTokenType(264);}
"overflow" {return tt.getKeywordTokenType(265);}
"overriding" {return tt.getKeywordTokenType(266);}
"package" {return tt.getKeywordTokenType(267);}
"parallel_enable" {return tt.getKeywordTokenType(268);}
"parameters" {return tt.getKeywordTokenType(269);}
"parent" {return tt.getKeywordTokenType(270);}
"partition" {return tt.getKeywordTokenType(271);}
"passing" {return tt.getKeywordTokenType(272);}
"path" {return tt.getKeywordTokenType(273);}
"pctfree" {return tt.getKeywordTokenType(274);}
"percent" {return tt.getKeywordTokenType(275);}
"phi_coefficient" {return tt.getKeywordTokenType(276);}
"pipe" {return tt.getKeywordTokenType(277);}
"pipelined" {return tt.getKeywordTokenType(278);}
"pivot" {return tt.getKeywordTokenType(279);}
"positive" {return tt.getKeywordTokenType(280);}
"positiven" {return tt.getKeywordTokenType(281);}
"power" {return tt.getKeywordTokenType(282);}
"pragma" {return tt.getKeywordTokenType(283);}
"preceding" {return tt.getKeywordTokenType(284);}
"present" {return tt.getKeywordTokenType(285);}
"prior" {return tt.getKeywordTokenType(286);}
"private" {return tt.getKeywordTokenType(287);}
"procedure" {return tt.getKeywordTokenType(288);}
"public" {return tt.getKeywordTokenType(289);}
"raise" {return tt.getKeywordTokenType(290);}
"range" {return tt.getKeywordTokenType(291);}
"read" {return tt.getKeywordTokenType(292);}
"record" {return tt.getKeywordTokenType(293);}
"ref" {return tt.getKeywordTokenType(294);}
"reference" {return tt.getKeywordTokenType(295);}
"referencing" {return tt.getKeywordTokenType(296);}
"regexp_like" {return tt.getKeywordTokenType(297);}
"reject" {return tt.getKeywordTokenType(298);}
"release" {return tt.getKeywordTokenType(299);}
"relies_on" {return tt.getKeywordTokenType(300);}
"remainder" {return tt.getKeywordTokenType(301);}
"rename" {return tt.getKeywordTokenType(302);}
"replace" {return tt.getKeywordTokenType(303);}
"restrict_references" {return tt.getKeywordTokenType(304);}
"result" {return tt.getKeywordTokenType(305);}
"result_cache" {return tt.getKeywordTokenType(306);}
"return" {return tt.getKeywordTokenType(307);}
"returning" {return tt.getKeywordTokenType(308);}
"reverse" {return tt.getKeywordTokenType(309);}
"revoke" {return tt.getKeywordTokenType(310);}
"right" {return tt.getKeywordTokenType(311);}
"rnds" {return tt.getKeywordTokenType(312);}
"rnps" {return tt.getKeywordTokenType(313);}
"rollback" {return tt.getKeywordTokenType(314);}
"rollup" {return tt.getKeywordTokenType(315);}
"row" {return tt.getKeywordTokenType(316);}
"rowcount" {return tt.getKeywordTokenType(317);}
"rownum" {return tt.getKeywordTokenType(318);}
"rows" {return tt.getKeywordTokenType(319);}
"rowtype" {return tt.getKeywordTokenType(320);}
"rules" {return tt.getKeywordTokenType(321);}
"sample" {return tt.getKeywordTokenType(322);}
"save" {return tt.getKeywordTokenType(323);}
"savepoint" {return tt.getKeywordTokenType(324);}
"schema" {return tt.getKeywordTokenType(325);}
"schemacheck" {return tt.getKeywordTokenType(326);}
"scn" {return tt.getKeywordTokenType(327);}
"second" {return tt.getKeywordTokenType(328);}
"seed" {return tt.getKeywordTokenType(329);}
"segment" {return tt.getKeywordTokenType(330);}
"select" {return tt.getKeywordTokenType(331);}
"self" {return tt.getKeywordTokenType(332);}
"separate" {return tt.getKeywordTokenType(333);}
"sequential" {return tt.getKeywordTokenType(334);}
"serializable" {return tt.getKeywordTokenType(335);}
"serially_reusable" {return tt.getKeywordTokenType(336);}
"servererror" {return tt.getKeywordTokenType(337);}
"set" {return tt.getKeywordTokenType(338);}
"sets" {return tt.getKeywordTokenType(339);}
"share" {return tt.getKeywordTokenType(340);}
"show" {return tt.getKeywordTokenType(341);}
"shutdown" {return tt.getKeywordTokenType(342);}
"siblings" {return tt.getKeywordTokenType(343);}
"sig" {return tt.getKeywordTokenType(344);}
"single" {return tt.getKeywordTokenType(345);}
"size" {return tt.getKeywordTokenType(346);}
"some" {return tt.getKeywordTokenType(347);}
"space" {return tt.getKeywordTokenType(348);}
"sql" {return tt.getKeywordTokenType(349);}
"sqlcode" {return tt.getKeywordTokenType(350);}
"sqlerrm" {return tt.getKeywordTokenType(351);}
"standalone" {return tt.getKeywordTokenType(352);}
"start" {return tt.getKeywordTokenType(353);}
"startup" {return tt.getKeywordTokenType(354);}
"statement" {return tt.getKeywordTokenType(355);}
"static" {return tt.getKeywordTokenType(356);}
"statistic" {return tt.getKeywordTokenType(357);}
"statistics" {return tt.getKeywordTokenType(358);}
"strict" {return tt.getKeywordTokenType(359);}
"struct" {return tt.getKeywordTokenType(360);}
"submultiset" {return tt.getKeywordTokenType(361);}
"subpartition" {return tt.getKeywordTokenType(362);}
"subtype" {return tt.getKeywordTokenType(363);}
"successful" {return tt.getKeywordTokenType(364);}
"sum_squares_between" {return tt.getKeywordTokenType(365);}
"sum_squares_within" {return tt.getKeywordTokenType(366);}
"suspend" {return tt.getKeywordTokenType(367);}
"synonym" {return tt.getKeywordTokenType(368);}
"table" {return tt.getKeywordTokenType(369);}
"tdo" {return tt.getKeywordTokenType(370);}
"then" {return tt.getKeywordTokenType(371);}
"ties" {return tt.getKeywordTokenType(372);}
"time" {return tt.getKeywordTokenType(373);}
"timezone_abbr" {return tt.getKeywordTokenType(374);}
"timezone_hour" {return tt.getKeywordTokenType(375);}
"timezone_minute" {return tt.getKeywordTokenType(376);}
"timezone_region" {return tt.getKeywordTokenType(377);}
"to" {return tt.getKeywordTokenType(378);}
"trailing" {return tt.getKeywordTokenType(379);}
"transaction" {return tt.getKeywordTokenType(380);}
"trigger" {return tt.getKeywordTokenType(381);}
"truncate" {return tt.getKeywordTokenType(382);}
"trust" {return tt.getKeywordTokenType(383);}
"two_sided_prob" {return tt.getKeywordTokenType(384);}
"two_sided_sig" {return tt.getKeywordTokenType(385);}
"type" {return tt.getKeywordTokenType(386);}
"u_statistic" {return tt.getKeywordTokenType(387);}
"unbounded" {return tt.getKeywordTokenType(388);}
"unconditional" {return tt.getKeywordTokenType(389);}
"under" {return tt.getKeywordTokenType(390);}
"under_path" {return tt.getKeywordTokenType(391);}
"union" {return tt.getKeywordTokenType(392);}
"unique" {return tt.getKeywordTokenType(393);}
"unlimited" {return tt.getKeywordTokenType(394);}
"unpivot" {return tt.getKeywordTokenType(395);}
"until" {return tt.getKeywordTokenType(396);}
"update" {return tt.getKeywordTokenType(397);}
"updated" {return tt.getKeywordTokenType(398);}
"updating" {return tt.getKeywordTokenType(399);}
"upsert" {return tt.getKeywordTokenType(400);}
"use" {return tt.getKeywordTokenType(401);}
"user" {return tt.getKeywordTokenType(402);}
"using" {return tt.getKeywordTokenType(403);}
"validate" {return tt.getKeywordTokenType(404);}
"value" {return tt.getKeywordTokenType(405);}
"values" {return tt.getKeywordTokenType(406);}
"variable" {return tt.getKeywordTokenType(407);}
"varray" {return tt.getKeywordTokenType(408);}
"varying" {return tt.getKeywordTokenType(409);}
"version" {return tt.getKeywordTokenType(410);}
"versions" {return tt.getKeywordTokenType(411);}
"view" {return tt.getKeywordTokenType(412);}
"wait" {return tt.getKeywordTokenType(413);}
"wellformed" {return tt.getKeywordTokenType(414);}
"when" {return tt.getKeywordTokenType(415);}
"whenever" {return tt.getKeywordTokenType(416);}
"where" {return tt.getKeywordTokenType(417);}
"while" {return tt.getKeywordTokenType(418);}
"with" {return tt.getKeywordTokenType(419);}
"within" {return tt.getKeywordTokenType(420);}
"without" {return tt.getKeywordTokenType(421);}
"wnds" {return tt.getKeywordTokenType(422);}
"wnps" {return tt.getKeywordTokenType(423);}
"work" {return tt.getKeywordTokenType(424);}
"write" {return tt.getKeywordTokenType(425);}
"wrapped" {return tt.getKeywordTokenType(426);}
"wrapper" {return tt.getKeywordTokenType(427);}
"xml" {return tt.getKeywordTokenType(428);}
"xmlnamespaces" {return tt.getKeywordTokenType(429);}
"year" {return tt.getKeywordTokenType(430);}
"yes" {return tt.getKeywordTokenType(431);}
"zone" {return tt.getKeywordTokenType(432);}
"false" {return tt.getKeywordTokenType(433);}
"true" {return tt.getKeywordTokenType(434);}





"abs" {return tt.getFunctionTokenType(0);}
"acos" {return tt.getFunctionTokenType(1);}
"add_months" {return tt.getFunctionTokenType(2);}
"appendchildxml" {return tt.getFunctionTokenType(3);}
"ascii" {return tt.getFunctionTokenType(4);}
"asciistr" {return tt.getFunctionTokenType(5);}
"asin" {return tt.getFunctionTokenType(6);}
"atan" {return tt.getFunctionTokenType(7);}
"atan2" {return tt.getFunctionTokenType(8);}
"avg" {return tt.getFunctionTokenType(9);}
"bfilename" {return tt.getFunctionTokenType(10);}
"bin_to_num" {return tt.getFunctionTokenType(11);}
"bitand" {return tt.getFunctionTokenType(12);}
"cardinality" {return tt.getFunctionTokenType(13);}
"cast" {return tt.getFunctionTokenType(14);}
"ceil" {return tt.getFunctionTokenType(15);}
"chartorowid" {return tt.getFunctionTokenType(16);}
"chr" {return tt.getFunctionTokenType(17);}
"compose" {return tt.getFunctionTokenType(18);}
"concat" {return tt.getFunctionTokenType(19);}
"convert" {return tt.getFunctionTokenType(20);}
"corr" {return tt.getFunctionTokenType(21);}
"corr_k" {return tt.getFunctionTokenType(22);}
"corr_s" {return tt.getFunctionTokenType(23);}
"cos" {return tt.getFunctionTokenType(24);}
"cosh" {return tt.getFunctionTokenType(25);}
"covar_pop" {return tt.getFunctionTokenType(26);}
"covar_samp" {return tt.getFunctionTokenType(27);}
"cume_dist" {return tt.getFunctionTokenType(28);}
"current_date" {return tt.getFunctionTokenType(29);}
"current_timestamp" {return tt.getFunctionTokenType(30);}
"cv" {return tt.getFunctionTokenType(31);}
"dbtimezone" {return tt.getFunctionTokenType(32);}
"dbtmezone" {return tt.getFunctionTokenType(33);}
"decode" {return tt.getFunctionTokenType(34);}
"decompose" {return tt.getFunctionTokenType(35);}
"deletexml" {return tt.getFunctionTokenType(36);}
"depth" {return tt.getFunctionTokenType(37);}
"deref" {return tt.getFunctionTokenType(38);}
"empty_blob" {return tt.getFunctionTokenType(39);}
"empty_clob" {return tt.getFunctionTokenType(40);}
"existsnode" {return tt.getFunctionTokenType(41);}
"exp" {return tt.getFunctionTokenType(42);}
"extract" {return tt.getFunctionTokenType(43);}
"extractvalue" {return tt.getFunctionTokenType(44);}
"first_value" {return tt.getFunctionTokenType(45);}
"floor" {return tt.getFunctionTokenType(46);}
"from_tz" {return tt.getFunctionTokenType(47);}
"greatest" {return tt.getFunctionTokenType(48);}
"group_id" {return tt.getFunctionTokenType(49);}
"grouping" {return tt.getFunctionTokenType(50);}
"grouping_id" {return tt.getFunctionTokenType(51);}
"hextoraw" {return tt.getFunctionTokenType(52);}
"initcap" {return tt.getFunctionTokenType(53);}
"insertchildxml" {return tt.getFunctionTokenType(54);}
"insertchildxmlafter" {return tt.getFunctionTokenType(55);}
"insertchildxmlbefore" {return tt.getFunctionTokenType(56);}
"insertxmlafter" {return tt.getFunctionTokenType(57);}
"insertxmlbefore" {return tt.getFunctionTokenType(58);}
"instr" {return tt.getFunctionTokenType(59);}
"instr2" {return tt.getFunctionTokenType(60);}
"instr4" {return tt.getFunctionTokenType(61);}
"instrb" {return tt.getFunctionTokenType(62);}
"instrc" {return tt.getFunctionTokenType(63);}
"iteration_number" {return tt.getFunctionTokenType(64);}
"json_array" {return tt.getFunctionTokenType(65);}
"json_arrayagg" {return tt.getFunctionTokenType(66);}
"json_dataguide" {return tt.getFunctionTokenType(67);}
"json_object" {return tt.getFunctionTokenType(68);}
"json_objectagg" {return tt.getFunctionTokenType(69);}
"json_query" {return tt.getFunctionTokenType(70);}
"json_table" {return tt.getFunctionTokenType(71);}
"json_value" {return tt.getFunctionTokenType(72);}
"lag" {return tt.getFunctionTokenType(73);}
"last_day" {return tt.getFunctionTokenType(74);}
"last_value" {return tt.getFunctionTokenType(75);}
"lateral" {return tt.getFunctionTokenType(76);}
"lead" {return tt.getFunctionTokenType(77);}
"least" {return tt.getFunctionTokenType(78);}
"length" {return tt.getFunctionTokenType(79);}
"length2" {return tt.getFunctionTokenType(80);}
"length4" {return tt.getFunctionTokenType(81);}
"lengthb" {return tt.getFunctionTokenType(82);}
"lengthc" {return tt.getFunctionTokenType(83);}
"listagg" {return tt.getFunctionTokenType(84);}
"ln" {return tt.getFunctionTokenType(85);}
"lnnvl" {return tt.getFunctionTokenType(86);}
"localtimestamp" {return tt.getFunctionTokenType(87);}
"lower" {return tt.getFunctionTokenType(88);}
"lpad" {return tt.getFunctionTokenType(89);}
"ltrim" {return tt.getFunctionTokenType(90);}
"make_ref" {return tt.getFunctionTokenType(91);}
"max" {return tt.getFunctionTokenType(92);}
"median" {return tt.getFunctionTokenType(93);}
"min" {return tt.getFunctionTokenType(94);}
"mod" {return tt.getFunctionTokenType(95);}
"months_between" {return tt.getFunctionTokenType(96);}
"nanvl" {return tt.getFunctionTokenType(97);}
"nchr" {return tt.getFunctionTokenType(98);}
"new_time" {return tt.getFunctionTokenType(99);}
"next_day" {return tt.getFunctionTokenType(100);}
"nls_charset_decl_len" {return tt.getFunctionTokenType(101);}
"nls_charset_id" {return tt.getFunctionTokenType(102);}
"nls_charset_name" {return tt.getFunctionTokenType(103);}
"nls_initcap" {return tt.getFunctionTokenType(104);}
"nls_lower" {return tt.getFunctionTokenType(105);}
"nls_upper" {return tt.getFunctionTokenType(106);}
"nlssort" {return tt.getFunctionTokenType(107);}
"ntile" {return tt.getFunctionTokenType(108);}
"nullif" {return tt.getFunctionTokenType(109);}
"numtodsinterval" {return tt.getFunctionTokenType(110);}
"numtoyminterval" {return tt.getFunctionTokenType(111);}
"nvl" {return tt.getFunctionTokenType(112);}
"nvl2" {return tt.getFunctionTokenType(113);}
"ora_hash" {return tt.getFunctionTokenType(114);}
"percent_rank" {return tt.getFunctionTokenType(115);}
"percentile_cont" {return tt.getFunctionTokenType(116);}
"percentile_disc" {return tt.getFunctionTokenType(117);}
"powermultiset" {return tt.getFunctionTokenType(118);}
"powermultiset_by_cardinality" {return tt.getFunctionTokenType(119);}
"presentnnv" {return tt.getFunctionTokenType(120);}
"presentv" {return tt.getFunctionTokenType(121);}
"previous" {return tt.getFunctionTokenType(122);}
"rank" {return tt.getFunctionTokenType(123);}
"ratio_to_report" {return tt.getFunctionTokenType(124);}
"rawtohex" {return tt.getFunctionTokenType(125);}
"rawtonhex" {return tt.getFunctionTokenType(126);}
"reftohex" {return tt.getFunctionTokenType(127);}
"regexp_instr" {return tt.getFunctionTokenType(128);}
"regexp_replace" {return tt.getFunctionTokenType(129);}
"regexp_substr" {return tt.getFunctionTokenType(130);}
"regr_avgx" {return tt.getFunctionTokenType(131);}
"regr_avgy" {return tt.getFunctionTokenType(132);}
"regr_count" {return tt.getFunctionTokenType(133);}
"regr_intercept" {return tt.getFunctionTokenType(134);}
"regr_r2" {return tt.getFunctionTokenType(135);}
"regr_slope" {return tt.getFunctionTokenType(136);}
"regr_sxx" {return tt.getFunctionTokenType(137);}
"regr_sxy" {return tt.getFunctionTokenType(138);}
"regr_syy" {return tt.getFunctionTokenType(139);}
"round" {return tt.getFunctionTokenType(140);}
"row_number" {return tt.getFunctionTokenType(141);}
"rowidtochar" {return tt.getFunctionTokenType(142);}
"rowidtonchar" {return tt.getFunctionTokenType(143);}
"rpad" {return tt.getFunctionTokenType(144);}
"rtrim" {return tt.getFunctionTokenType(145);}
"scn_to_timestamp" {return tt.getFunctionTokenType(146);}
"sessiontimezone" {return tt.getFunctionTokenType(147);}
"sign" {return tt.getFunctionTokenType(148);}
"sin" {return tt.getFunctionTokenType(149);}
"sinh" {return tt.getFunctionTokenType(150);}
"soundex" {return tt.getFunctionTokenType(151);}
"sqrt" {return tt.getFunctionTokenType(152);}
"stats_binomial_test" {return tt.getFunctionTokenType(153);}
"stats_crosstab" {return tt.getFunctionTokenType(154);}
"stats_f_test" {return tt.getFunctionTokenType(155);}
"stats_ks_test" {return tt.getFunctionTokenType(156);}
"stats_mode" {return tt.getFunctionTokenType(157);}
"stats_mw_test" {return tt.getFunctionTokenType(158);}
"stats_one_way_anova" {return tt.getFunctionTokenType(159);}
"stats_t_test_indep" {return tt.getFunctionTokenType(160);}
"stats_t_test_indepu" {return tt.getFunctionTokenType(161);}
"stats_t_test_one" {return tt.getFunctionTokenType(162);}
"stats_t_test_paired" {return tt.getFunctionTokenType(163);}
"stats_wsr_test" {return tt.getFunctionTokenType(164);}
"stddev" {return tt.getFunctionTokenType(165);}
"stddev_pop" {return tt.getFunctionTokenType(166);}
"stddev_samp" {return tt.getFunctionTokenType(167);}
"substr" {return tt.getFunctionTokenType(168);}
"substr2" {return tt.getFunctionTokenType(169);}
"substr4" {return tt.getFunctionTokenType(170);}
"substrb" {return tt.getFunctionTokenType(171);}
"substrc" {return tt.getFunctionTokenType(172);}
"sum" {return tt.getFunctionTokenType(173);}
"sys_connect_by_path" {return tt.getFunctionTokenType(174);}
"sys_context" {return tt.getFunctionTokenType(175);}
"sys_dburigen" {return tt.getFunctionTokenType(176);}
"sys_extract_utc" {return tt.getFunctionTokenType(177);}
"sys_guid" {return tt.getFunctionTokenType(178);}
"sys_typeid" {return tt.getFunctionTokenType(179);}
"sys_xmlagg" {return tt.getFunctionTokenType(180);}
"sys_xmlgen" {return tt.getFunctionTokenType(181);}
"sysdate" {return tt.getFunctionTokenType(182);}
"systimestamp" {return tt.getFunctionTokenType(183);}
"tan" {return tt.getFunctionTokenType(184);}
"tanh" {return tt.getFunctionTokenType(185);}
"timestamp_to_scn" {return tt.getFunctionTokenType(186);}
"to_binary_double" {return tt.getFunctionTokenType(187);}
"to_binary_float" {return tt.getFunctionTokenType(188);}
"to_char" {return tt.getFunctionTokenType(189);}
"to_clob" {return tt.getFunctionTokenType(190);}
"to_date" {return tt.getFunctionTokenType(191);}
"to_dsinterval" {return tt.getFunctionTokenType(192);}
"to_lob" {return tt.getFunctionTokenType(193);}
"to_multi_byte" {return tt.getFunctionTokenType(194);}
"to_nchar" {return tt.getFunctionTokenType(195);}
"to_nclob" {return tt.getFunctionTokenType(196);}
"to_number" {return tt.getFunctionTokenType(197);}
"to_single_byte" {return tt.getFunctionTokenType(198);}
"to_timestamp" {return tt.getFunctionTokenType(199);}
"to_timestamp_tz" {return tt.getFunctionTokenType(200);}
"to_yminterval" {return tt.getFunctionTokenType(201);}
"translate" {return tt.getFunctionTokenType(202);}
"treat" {return tt.getFunctionTokenType(203);}
"trim" {return tt.getFunctionTokenType(204);}
"trunc" {return tt.getFunctionTokenType(205);}
"tz_offset" {return tt.getFunctionTokenType(206);}
"uid" {return tt.getFunctionTokenType(207);}
"unistr" {return tt.getFunctionTokenType(208);}
"updatexml" {return tt.getFunctionTokenType(209);}
"upper" {return tt.getFunctionTokenType(210);}
"userenv" {return tt.getFunctionTokenType(211);}
"var_pop" {return tt.getFunctionTokenType(212);}
"var_samp" {return tt.getFunctionTokenType(213);}
"variance" {return tt.getFunctionTokenType(214);}
"vsize" {return tt.getFunctionTokenType(215);}
"width_bucket" {return tt.getFunctionTokenType(216);}
"xmlagg" {return tt.getFunctionTokenType(217);}
"xmlattributes" {return tt.getFunctionTokenType(218);}
"xmlcast" {return tt.getFunctionTokenType(219);}
"xmlcdata" {return tt.getFunctionTokenType(220);}
"xmlcolattval" {return tt.getFunctionTokenType(221);}
"xmlcomment" {return tt.getFunctionTokenType(222);}
"xmlconcat" {return tt.getFunctionTokenType(223);}
"xmldiff" {return tt.getFunctionTokenType(224);}
"xmlelement" {return tt.getFunctionTokenType(225);}
"xmlforest" {return tt.getFunctionTokenType(226);}
"xmlisvalid" {return tt.getFunctionTokenType(227);}
"xmlparse" {return tt.getFunctionTokenType(228);}
"xmlpatch" {return tt.getFunctionTokenType(229);}
"xmlpi" {return tt.getFunctionTokenType(230);}
"xmlquery" {return tt.getFunctionTokenType(231);}
"xmlroot" {return tt.getFunctionTokenType(232);}
"xmlsequence" {return tt.getFunctionTokenType(233);}
"xmlserialize" {return tt.getFunctionTokenType(234);}
"xmltable" {return tt.getFunctionTokenType(235);}
"xmltransform" {return tt.getFunctionTokenType(236);}









"access_into_null" {return tt.getExceptionTokenType(0);}
"case_not_found" {return tt.getExceptionTokenType(1);}
"collection_is_null" {return tt.getExceptionTokenType(2);}
"cursor_already_open" {return tt.getExceptionTokenType(3);}
"dup_val_on_index" {return tt.getExceptionTokenType(4);}
"invalid_cursor" {return tt.getExceptionTokenType(5);}
"invalid_number" {return tt.getExceptionTokenType(6);}
"login_denied" {return tt.getExceptionTokenType(7);}
"no_data_found" {return tt.getExceptionTokenType(8);}
"not_logged_on" {return tt.getExceptionTokenType(9);}
"program_error" {return tt.getExceptionTokenType(10);}
"rowtype_mismatch" {return tt.getExceptionTokenType(11);}
"self_is_null" {return tt.getExceptionTokenType(12);}
"storage_error" {return tt.getExceptionTokenType(13);}
"subscript_beyond_count" {return tt.getExceptionTokenType(14);}
"subscript_outside_limit" {return tt.getExceptionTokenType(15);}
"sys_invalid_rowid" {return tt.getExceptionTokenType(16);}
"timeout_on_resource" {return tt.getExceptionTokenType(17);}
"too_many_rows" {return tt.getExceptionTokenType(18);}
"value_error" {return tt.getExceptionTokenType(19);}
"zero_divide" {return tt.getExceptionTokenType(20);}



{IDENTIFIER}           { return tt.getSharedTokenTypes().getIdentifier(); }
{QUOTED_IDENTIFIER}    { return tt.getSharedTokenTypes().getQuotedIdentifier(); }
.                      { return tt.getSharedTokenTypes().getIdentifier(); }


