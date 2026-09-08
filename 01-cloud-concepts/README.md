# 01 — Cloud Concepts

## Cloud Computing

Cloud Computing é a utilização de recursos de computação pela internet, sob demanda, sem a necessidade de manter toda a infraestrutura física localmente.

Entre os principais recursos estão:

* Computação
* Armazenamento
* Bancos de dados
* Redes
* Segurança
* Monitoramento

A principal ideia é poder utilizar esses recursos conforme a necessidade, pagando pelo que for consumido.

---

## CapEx vs OpEx

### CapEx — Capital Expenditure

É o investimento inicial necessário para adquirir infraestrutura e equipamentos.

Exemplo:

* Comprar servidores físicos;
* Comprar equipamentos de rede;
* Montar e manter um Data Center.

Nesse modelo, existe um investimento inicial alto e a empresa precisa prever sua capacidade futura.

### OpEx — Operational Expenditure

São os custos relacionados à operação e utilização dos recursos.

Na nuvem, podemos contratar recursos sob demanda e pagar conforme o consumo, sem precisar comprar toda a infraestrutura antecipadamente.

**Resumo:**

| CapEx                            | OpEx                          |
| -------------------------------- | ----------------------------- |
| Investimento inicial             | Despesa operacional           |
| Compra de infraestrutura         | Uso/consumo de recursos       |
| Maior investimento antecipado    | Pagamento conforme utilização |
| Capacidade precisa ser planejada | Capacidade pode ser ajustada  |

---

## Pay-as-you-go

O modelo **pay-as-you-go** significa pagar de acordo com a utilização dos recursos.

Em vez de comprar antecipadamente uma infraestrutura que pode ficar subutilizada, podemos utilizar os recursos necessários e pagar pelo consumo.

Isso permite maior flexibilidade e reduz a necessidade de grandes investimentos iniciais.

---

## Scalability

**Scalability (escalabilidade)** é a capacidade de aumentar ou diminuir a capacidade de um sistema para atender diferentes necessidades.

Existem principalmente duas formas:

### Vertical Scaling

Aumentar os recursos de uma máquina existente.

Exemplo:

```text
EC2 pequena
    ↓
EC2 maior
```

Podemos aumentar CPU, memória ou outros recursos da instância.

### Horizontal Scaling

Adicionar ou remover instâncias.

Exemplo:

```text
1 EC2
 ↓
2 EC2
 ↓
10 EC2
```

Nesse caso, aumentamos a quantidade de máquinas que executam a aplicação.

---

## Elasticity

**Elasticity (elasticidade)** é a capacidade de ajustar dinamicamente os recursos de acordo com a demanda.

Por exemplo:

```text
Baixa demanda → 2 instâncias
Alta demanda  → 10 instâncias
Baixa demanda → 2 instâncias
```

O ponto principal é o ajuste automático ou dinâmico da capacidade conforme a necessidade.

### Scalability x Elasticity

* **Scalability:** capacidade de aumentar ou diminuir a capacidade.
* **Elasticity:** capacidade de ajustar essa capacidade dinamicamente de acordo com a demanda.

Portanto, elasticidade está relacionada à adaptação dinâmica à variação da demanda.

---

## High Availability

**High Availability (alta disponibilidade)** é uma característica de uma arquitetura projetada para minimizar interrupções e manter o sistema disponível mesmo quando ocorrem falhas.

Por exemplo, utilizar apenas uma instância EC2 cria um ponto único de falha:

```text
Usuários
   ↓
 EC2
```

Se essa instância falhar, a aplicação pode ficar indisponível.

Uma arquitetura mais resiliente poderia utilizar múltiplas instâncias em diferentes Availability Zones:

```text
             ┌── EC2 ── AZ A
Usuários → LB│
             └── EC2 ── AZ B
```

Assim, uma falha em uma Availability Zone não necessariamente interrompe toda a aplicação.

---

## Fault Tolerance

**Fault Tolerance (tolerância a falhas)** é a capacidade de um sistema continuar funcionando mesmo quando determinados componentes falham.

A diferença principal é:

* **High Availability:** busca minimizar o tempo de indisponibilidade.
* **Fault Tolerance:** busca continuar funcionando mesmo diante de uma falha.

Uma arquitetura pode buscar ambos os objetivos.

---

## AWS Region

Uma **AWS Region** é uma área geográfica onde a AWS possui infraestrutura de Data Centers.

Uma Region **não deve ser entendida simplesmente como um país**.

Dentro de uma Region existem várias Availability Zones.

Exemplo:

```text
Region
├── Availability Zone A
├── Availability Zone B
└── Availability Zone C
```

A escolha da Region pode considerar fatores como:

* Latência para os usuários;
* Disponibilidade dos serviços;
* Requisitos de conformidade;
* Localização dos dados;
* Custos.

---

## Availability Zone

Uma **Availability Zone (AZ)** é uma localização isolada dentro de uma AWS Region.

As AZs possuem infraestrutura independente e são projetadas para reduzir o impacto de falhas.

Por isso, distribuir aplicações entre diferentes AZs aumenta a resiliência da arquitetura.

Exemplo:

```text
AWS Region
├── AZ A
│   └── EC2
├── AZ B
│   └── EC2
└── AZ C
    └── EC2
```

Dessa forma, uma falha em uma AZ não precisa necessariamente afetar as outras.

---

## Edge Location

**Edge Locations** são pontos de presença da infraestrutura da AWS distribuídos geograficamente e próximos aos usuários.

Um dos principais serviços que utiliza essa infraestrutura é o **Amazon CloudFront**, que pode armazenar conteúdo em cache próximo dos usuários.

Exemplo:

```text
Usuário
   ↓
Edge Location
   ↓
CloudFront
   ↓
Origem
```

O objetivo principal é reduzir a latência e melhorar a experiência do usuário.

Edge Location não deve ser definido apenas como "um cache", pois é um ponto de presença utilizado por serviços como o CloudFront.

---

# Perguntas e respostas

## 1. Qual é a diferença entre CapEx e OpEx?

**Minha resposta:**

```
CapEx (Capital Expenditure) é o conceito de investir capital ou gastar o dinheiro para comprar toda a infraestrutura necessária para o negócio funcionar
enquanto no OpEx (Operation Expenditure) a preocupação é penas comprar e pagar pelo necessário para executar a operação necessária, também
entrando o conceito de pay as you go de não gastar dinheiro com recursos ociosos ou desnecessários
```

CapEx está relacionado ao investimento inicial para adquirir infraestrutura, como servidores e equipamentos.

OpEx está relacionado aos custos de operação e utilização dos recursos. Na nuvem, podemos utilizar recursos sob demanda e pagar conforme o consumo.

**Correção:** a ideia principal está correta. OpEx não significa necessariamente "comprar"; é uma despesa operacional. O modelo pay-as-you-go é uma aplicação comum desse conceito na nuvem.

---

## 2. Qual é a diferença entre Scalability e Elasticity?

**Minha resposta:**

```
Escalabilidade é o conceito de ter os recursos computacionais necessários para lidar audavelmente com picos de processamento elevados
enquanto elasticidade é o conceito de se moldar de acordo com a carga de processamento em execução, seja com a necessidade aumentar
recursos para altas cargas ou diminuir recursos para baixas cargas
```

Scalability é a capacidade de aumentar ou diminuir a capacidade de um sistema.

Elasticity é a capacidade de ajustar essa capacidade dinamicamente de acordo com a demanda.

A escalabilidade pode ser vertical, aumentando os recursos de uma máquina, ou horizontal, adicionando mais máquinas.

**Conclusão:** resposta correta. A principal palavra-chave para diferenciar os conceitos é **dinamicamente** no caso de Elasticity.

---

## 3. Por que utilizar duas EC2 em diferentes Availability Zones?

**Minha resposta:**

```
Porque as AZs são o "baixo nível" da infraestrutura global da AWS, onde os data centeres com todo o hardware estão montados e em execução
numa área específica de uma cidade/país, então escolher duas dessas áreas em diferentes locais físicos aumenta a segurança de
se acontecer uma desastre ambiental, falta de energia, etc numa área, provavelente a outra área por terem distâncias físicas significativamente
longas, ainda estará em operação e com isso garantindo a disponibilidade da aplicação em questão
```

Porque as Availability Zones possuem isolamento físico e de infraestrutura. Dessa forma, se ocorrer uma falha em uma AZ, como problemas de energia ou infraestrutura, a outra pode continuar funcionando.

Isso aumenta a disponibilidade e a resiliência da aplicação.

**Conclusão:** resposta correta. Distribuir recursos entre AZs reduz o impacto de falhas localizadas.

---

## 4. Qual é a diferença entre Region e Availability Zone?

**Minha resposta:**

```
Region é o conceito de mais auto nível, sendo por exemplo o país da resposta da pergunta 3 anterior e a AZ seria os vários territórios/cidades
em que estão fisicamente os data centeres com os toda a infraestrutura de hardware necessária
```

Uma Region representa uma área geográfica da AWS e contém várias Availability Zones.

Uma Availability Zone é uma localização isolada dentro de uma Region.

**Correção:** uma Region não deve ser definida como um país. Ela representa uma área geográfica da AWS, que pode ser associada a uma determinada região do mundo.

---

## 5. O que é uma Edge Location?

**Minha resposta:**

```
Serve como um mecanismo de cache para aumentar a rapidez de entrega de conteúdo para usuários de uma localização específica
```

É um ponto de presença da AWS localizado próximo aos usuários. Serviços como o CloudFront podem utilizar essas localizações para armazenar conteúdo em cache e entregá-lo com menor latência.

**Conclusão:** resposta correta. O cache é um dos usos, mas Edge Location é mais amplamente um ponto de presença da infraestrutura da AWS.

---

## 6. Se temos 2 EC2, aumentamos para 10 durante um pico e depois voltamos para 2, qual conceito está sendo aplicado?

**Minha resposta:**

```
O conceito que está sendo aplicado é o de Elasticidade
```

Isso representa elasticidade, porque a quantidade de instâncias está sendo ajustada de acordo com a demanda.

Também existe escalabilidade, pois a capacidade do sistema foi aumentada e depois reduzida.

**Conclusão:** resposta correta. Para a pergunta, o conceito principal é **Elasticity**, porque existe ajuste dinâmico conforme a demanda.

---

## 7. Uma aplicação possui apenas uma EC2. Ela possui alta disponibilidade?

**Minha resposta:**

```
Não, porque ela não estará devidamente preparada para todos os cenários de tolerância a falha (no caso de falhas físicas) ou recuperação de desastre
mesmo ela estando autamente otimizada para picos de processamento e falhas/bugs de software em tempo de execução
```

Não. Uma única EC2 representa um ponto único de falha. Se essa instância apresentar algum problema, a aplicação pode ficar indisponível.

Para aumentar a disponibilidade, podemos utilizar múltiplas instâncias, distribuídas em diferentes Availability Zones, juntamente com mecanismos como Load Balancer e Auto Scaling.

**Conclusão:** resposta correta. A arquitetura pode ser projetada para reduzir pontos únicos de falha e aumentar a disponibilidade.
