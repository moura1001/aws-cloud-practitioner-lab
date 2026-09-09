# Learning Log

Registro de evolução dos estudos para a certificação **AWS Certified Cloud Practitioner (CLF-C02)**.

---

# Day 01 — Cloud Concepts

**Status:** Concluído
**Resultado:** Bom domínio dos conceitos fundamentais, com pequenos ajustes de terminologia.

## Conceitos estudados

* Cloud Computing
* CapEx e OpEx
* Pay-as-you-go
* Scalability
* Elasticity
* High Availability
* Fault Tolerance
* Region
* Availability Zone
* Edge Location

## Principais aprendizados

### 1. Scalability x Elasticity

```text
Scalability
    ↓
Capacidade de aumentar ou diminuir recursos

Elasticity
    ↓
Ajuste dinâmico dos recursos conforme a demanda
```

**Palavra-chave:**
Elasticity → **dinamicamente**

---

### 2. High Availability x Fault Tolerance

```text
High Availability
    ↓
Minimizar / evitar indisponibilidade

Fault Tolerance
    ↓
Continuar funcionando mesmo diante de falhas
```

Os conceitos são relacionados, mas não são sinônimos.

---

### 3. Region x Availability Zone

```text
AWS Global Infrastructure
│
├── Region
│   │
│   ├── Availability Zone
│   ├── Availability Zone
│   └── Availability Zone
│
└── Edge Locations
```

**Region:** área geográfica da AWS.

**Availability Zone:** localização isolada dentro de uma Region.

**Edge Location:** ponto de presença próximo aos usuários, utilizado por serviços como o CloudFront.

---

## As 5 relações que preciso memorizar

### 1. Region → Availability Zone

Uma **Region contém múltiplas Availability Zones**.

```text
Region
├── AZ
├── AZ
└── AZ
```

---

### 2. Availability Zone → High Availability

Distribuir recursos entre diferentes AZs ajuda a evitar que uma falha localizada provoque a indisponibilidade completa da aplicação.

```text
AZ A → EC2
AZ B → EC2

       ↓
Maior disponibilidade
```

---

### 3. Edge Location → CloudFront → Menor latência

O CloudFront utiliza a infraestrutura de Edge Locations para aproximar o conteúdo dos usuários.

```text
Usuário
   ↓
Edge Location
   ↓
CloudFront
   ↓
Origem
```

---

### 4. Scalability → Elasticity

Scalability representa a capacidade de aumentar ou diminuir a capacidade.

Elasticity representa o ajuste dessa capacidade de forma dinâmica conforme a demanda.

```text
Scalability
    ↓
Pode aumentar/diminuir

Elasticity
    ↓
Aumenta/diminui conforme a demanda
```

---

### 5. High Availability → múltiplas AZs

Utilizar múltiplas AZs é uma estratégia para aumentar a disponibilidade e reduzir pontos únicos de falha.

```text
              ┌── AZ A → EC2
Usuários → LB ┤
              └── AZ B → EC2
```

---

## Erros/correções do Day 01

### Region não é simplesmente um país

A definição correta é **área geográfica da AWS**.

### Edge Location não é apenas cache

Edge Location é um **ponto de presença**. O cache é uma das utilizações, especialmente pelo CloudFront.

### Elasticity e Scalability não são sinônimos

Ambos estão relacionados à capacidade de ajustar recursos, mas **Elasticity enfatiza o ajuste dinâmico conforme a demanda**.

---

## Autoavaliação

**Resultado aproximado:** 89%

O conhecimento conceitual foi considerado bom. Os principais pontos para reforçar são:

* Region ≠ país;
* Edge Location ≠ somente cache;
* Elasticity → ajuste dinâmico;
* High Availability ≠ simplesmente "ter várias máquinas".

---

## Regra prática do Day 01

Ao analisar uma arquitetura AWS, perguntar:

1. **Onde?** → Region
2. **Em quais locais isolados?** → Availability Zones
3. **Como reduzir a latência para usuários?** → Edge Locations / CloudFront
4. **Como aumentar ou diminuir capacidade?** → Scalability
5. **Como ajustar essa capacidade conforme a demanda?** → Elasticity
6. **Como evitar indisponibilidade?** → High Availability
7. **Como continuar funcionando diante de falhas?** → Fault Tolerance

---

## Próximos passos

* [x] Consolidar os conceitos do Day 01
* [x] Registrar as anotações no `01-cloud-concepts/README.md`
* [x] Fazer commit e push
* [x] Iniciar Day 02

# Day 02 — AWS Global Infrastructure & Well-Architected Framework

**Status:** Concluído
**Resultado:** Excelente domínio conceitual, com pequenos ajustes de terminologia.

## Conceitos estudados

* AWS Region
* Availability Zone
* Edge Location
* AWS Global Infrastructure
* AWS Well-Architected Framework
* Operational Excellence
* Security
* Reliability
* Performance Efficiency
* Cost Optimization
* Sustainability

## Principais relações aprendidas

```text
Region
    ↓
Availability Zones
    ↓
Maior isolamento contra falhas
    ↓
Maior disponibilidade / resiliência
```

```text
Edge Location
    ↓
CloudFront
    ↓
Conteúdo mais próximo do usuário
    ↓
Menor latência
```

```text
Scalability
    ↓
Aumentar / diminuir capacidade

Elasticity
    ↓
Ajustar dinamicamente conforme a demanda
```

## Relação entre os pilares

```text
Operational Excellence
    → Como operar e melhorar?

Security
    → Como proteger?

Reliability
    → Como resistir e se recuperar de falhas?

Performance Efficiency
    → Como utilizar os recursos de forma eficiente?

Cost Optimization
    → Como evitar gastos desnecessários?

Sustainability
    → Como reduzir o impacto ambiental?
```

## Pontos que precisei corrigir

### Region não é um país

Region deve ser entendida como uma **área geográfica da AWS** que contém múltiplas Availability Zones.

### Reliability é o nome do pilar

Resiliência e tolerância a falhas são conceitos associados à Reliability, mas **não são nomes de pilares**.

### Performance Efficiency

O nome completo do pilar é **Performance Efficiency**, e não apenas "Performance".

### Security não trata apenas de controle de acesso

Security também envolve **auditoria, rastreabilidade, proteção de dados e identidade**.

Exemplo:

```text
Quem pode fazer?
    → Controle de acesso

Quem fez?
    → Auditoria / rastreabilidade
```

### Performance Efficiency × Reliability

```text
Sistema lento / recurso inadequado
    → Performance Efficiency

Sistema falhando / precisa se recuperar
    → Reliability
```

## Autoavaliação

**Resultado:** 9/10

Consegui identificar corretamente os seis pilares nas situações apresentadas.

Os principais ajustes foram relacionados à precisão da terminologia e à distinção entre conceitos próximos:

* Reliability × Resilience;
* Performance Efficiency × Reliability;
* Security × controle de acesso/auditoria.

## Regra prática

Ao analisar uma questão do Well-Architected Framework:

> **Primeiro identificar o problema que a arquitetura está tentando resolver; depois associá-lo ao pilar.**

Não começar procurando uma tecnologia AWS específica.

## Próximo passo

* [x] Estudar Global Infrastructure
* [x] Estudar Well-Architected Framework
* [x] Responder às questões conceituais
* [x] Registrar correções
* [ ] Iniciar próximo módulo

