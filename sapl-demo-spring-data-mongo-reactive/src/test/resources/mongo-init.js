db = db.getSiblingDB('sapl4db');

db.book.insertMany([
    { _id: NumberLong(1), name: 'book1', category: 1 },
    { _id: NumberLong(2), name: 'book2', category: 1 },
    { _id: NumberLong(3), name: 'book3', category: 2 },
    { _id: NumberLong(4), name: 'book4', category: 3 },
    { _id: NumberLong(5), name: 'book5', category: 4 },
    { _id: NumberLong(6), name: 'book6', category: 5 }
]);
