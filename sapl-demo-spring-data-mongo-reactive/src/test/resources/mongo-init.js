db = db.getSiblingDB('sapl4db');

db.createCollection('users');

db.users.insertMany([ {
    "_id" : ObjectId("64de2fb8375aabd24878daa4"),
    "firstname" : "Malinda",
    "lastname" : "Perrot",
    "age" : 53,
    "role" : "ADMIN",
    "active" : true
}, {
    "_id" : ObjectId("64de3bd9fbf82799677ed336"),
    "firstname" : "Emerson",
    "lastname" : "Rowat",
    "age" : 82,
    "role" : "USER",
    "active" : false
}, {
    "_id" : ObjectId("64de3bd9fbf82799677ed337"),
    "firstname" : "Yul",
    "lastname" : "Barukh",
    "age" : 79,
    "role" : "ADMIN",
    "active" : true
}, {
    "_id" : ObjectId("64de3bd9fbf82799677ed338"),
    "firstname" : "Terrel",
    "lastname" : "Woodings",
    "age" : 96,
    "role" : "USER",
    "active" : true
}, {
    "_id" : ObjectId("64de3bd9fbf82799677ed339"),
    "firstname" : "Martino",
    "lastname" : "Bartolijn",
    "age" : 33,
    "role" : "USER",
    "active" : false
}, {
    "_id" : ObjectId("64de3bd9fbf82799677ed33a"),
    "firstname" : "Konstantine",
    "lastname" : "Hampton",
    "age" : 96,
    "role" : "USER",
    "active" : true
}, {
    "_id" : ObjectId("64de3bd9fbf82799677ed33b"),
    "firstname" : "Cathleen",
    "lastname" : "Simms",
    "age" : 25,
    "role" : "ADMIN",
    "active" : false
}, {
    "_id" : ObjectId("64de3bd9fbf82799677ed33c"),
    "firstname" : "Adolphe",
    "lastname" : "Streeton",
    "age" : 46,
    "role" : "USER",
    "active" : true
}, {
    "_id" : ObjectId("64de3bd9fbf82799677ed33d"),
    "firstname" : "Alessandro",
    "lastname" : "Tomaskov",
    "age" : 64,
    "role" : "USER",
    "active" : true
}, {
    "_id" : ObjectId("64de3bd9fbf82799677ed33e"),
    "firstname" : "Hobie",
    "lastname" : "Maddinon",
    "age" : 32,
    "role" : "ADMIN",
    "active" : false
}, {
    "_id" : ObjectId("64de3bd9fbf82799677ed33f"),
    "firstname" : "Franni",
    "lastname" : "Mingey",
    "age" : 57,
    "role" : "ADMIN",
    "active" : false
}, {
    "_id" : ObjectId("64de3bd9fbf82799677ed340"),
    "firstname" : "Giraldo",
    "lastname" : "Scade",
    "age" : 83,
    "role" : "ADMIN",
    "active" : true
}, {
    "_id" : ObjectId("64de3bd9fbf82799677ed341"),
    "firstname" : "Pooh",
    "lastname" : "Cocks",
    "age" : 19,
    "role" : "ADMIN",
    "active" : true
}, {
    "_id" : ObjectId("64de3bd9fbf82799677ed342"),
    "firstname" : "Mario",
    "lastname" : "Albinson",
    "age" : 54,
    "role" : "USER",
    "active" : false
}, {
    "_id" : ObjectId("64de3bd9fbf82799677ed343"),
    "firstname" : "Olav",
    "lastname" : "Hoopper",
    "age" : 32,
    "role" : "ADMIN",
    "active" : true
}, {
    "_id" : ObjectId("64de3bd9fbf82799677ed344"),
    "firstname" : "Tuckie",
    "lastname" : "Morfell",
    "age" : 35,
    "role" : "USER",
    "active" : true
}, {
    "_id" : ObjectId("64de3bd9fbf82799677ed345"),
    "firstname" : "Sylas",
    "lastname" : "Bickerstasse",
    "age" : 66,
    "role" : "USER",
    "active" : true
}, {
    "_id" : ObjectId("64de3bd9fbf82799677ed346"),
    "firstname" : "Kacey",
    "lastname" : "Angell",
    "age" : 94,
    "role" : "USER",
    "active" : false
} ]);