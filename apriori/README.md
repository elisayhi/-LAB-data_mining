Dataset:
  transaction database
  format: a line means a transaction data, each item seperated by space

Command:
  compile: javac Apriori.java
  excute: java Apriori filename.txt

Result:
  the result will output to the file named filename_result.txt
  Output infromation:
    File (input filename)
    min support
    amount of frequent itemesets
    runtime and memory of generating frequent itemsets
    amount of association rules
    runtime and memory of generating association rules
