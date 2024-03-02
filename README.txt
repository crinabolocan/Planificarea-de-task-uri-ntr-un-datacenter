Bolocan Crina-Maria 331CB

Am inceput in clasa MyDispatcher, unde pentru fiecare tip de algoritm
am apelat metoda necesara pe taskul respectiv.
Pentru politica RoundRobin, am respectat formula din cerinta, verificand care e
ultimul nod ocupat. Pentru politica ShoertestQueue, am verificat daca coada
recenta pe host e mai mica decat cea mai mica deja calculata si am facut
imparteala. Similar si pentru LeastWorkLeft, doar ca am tinut cont de munca
ramasa. Iar pentru SizeIntervalTaskAssignment, am luat cazurile specifice
tipului de task ca sa stiu unde sa adaug(SHORT, MEDIUM, LONG).

In clasa MyHost, am declarat o coada blocanta de prioritati de tip Task
pentru a stoca taskurile, si le sortez dupa prioritati in clasa Comparator,
ca sa tin cont de care trebuie executat primul. In metoda run(), am calculat
cat e de asteptat pe fiecare task ca sa stiu cat pun sleep, dar sa tin cont
si daca task-ul a fost preemtat sau nu. In metoda addTask() am verificat daca
taskul nou aparut la momentul de timp este cu o prioritate mai buna si daca
taskul curent e preemtabil ca sa ii recalculez timpul ramas in executie si sa
il repun in coada, dupa cel cu prioritate mai mare. In getQueueSize(), am
calculat marimea cozii in functie de ce e in prezent si ce mai e de executat.
Similar si cu getWorkLeft(), doar ca tin cont de timpul ramas pentru executie.
Si la final shutdown().