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
* [ ] Iniciar Day 02
