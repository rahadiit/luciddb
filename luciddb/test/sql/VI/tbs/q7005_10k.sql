set schema 's';

SELECT KSEQ, K100, K2 FROM BENCH10K 
WHERE K100 > 20 AND K2 > 1 AND K5 > 4
order by 1;
