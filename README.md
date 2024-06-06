## Parcial Vacaciones

[Link al enunciado](https://docs.google.com/document/d/12UdyTUUs1gVHc4ukTujKgsb6D86y5vfyiDiEuA_AQ-8/edit?usp=sharing)

## Diagrama de clases

## Desiciones de diseño

#### Punto 1
- Siendo que los tres tipos de lugares comparten una condición para ***ser divertidos*** y comparten la property ***nombre***, extienden la ***abstract class Lugar***. Eso me permite evitar la repetición de codigo con un Template Method debido a que la parte fija del algoritmo es parte de la clase padre.

#### Punto 1.3
- Aún que ***marPeligroso*** podría usarse directamente en el condicional, preferí mantener ese valor privado y hacerle un getter. Lo mismo pasa para ***peatonal***.

#### Punto 2
- A pesar de que la preferencia no se inicialice en su definición, creé una preferencia ***SinPreferencia()*** que devuelve siempre ***true*** a modo de preferencia default o de ***Null-Object***.

- 
