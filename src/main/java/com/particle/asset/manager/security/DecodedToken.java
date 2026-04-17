package com.particle.asset.manager.security;

// Un record è una classe immutabile introdotta in Java 16 e si utilizza per contenere dati semplici.
// Effettua, in automatico: costruttore, Getter, equals, hashCode e toString
public record DecodedToken(String oid, String email, String displayName) { }
