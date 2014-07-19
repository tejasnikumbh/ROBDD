ROBDD
=====

This package implements the following operations on ROBDDs [described File Wise]
 * ROBDD.java
   - Implements the Build Operation. Enables user to build ROBDD from a Boolean Expression.
 * ROBDDComputed.java
   - Can be used to compute resulting ROBDD by passing in two ROBDDs and an operation to be performed on the two ROBDDs. 
 * ROBDDRestricted.java
   - Can be used to build an ROBDD with truth assignments of certain variables given and certain other not. Computes resulting ROBDD, when ROBDD to be restricted and the restricting variable (as well as its value) is passed in.
 * ROBDDSATSolver.java
   - Can be used to build an ROBDD from a Boolean Expression as well as perform satisfiablity tests on the ROBDD. SATCount and ANYSAT methods provide number of satisfiable truth assignments as well as some particular truth assignment that satisfies the ROBDD, if applicable, respectively.
   
Reference : Andersen's Notes
