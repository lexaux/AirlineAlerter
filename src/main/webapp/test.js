db.wizzair.find().forEach(function (obj) {
    var v = obj["price"];
    if ('string' === (typeof v)) {
        var cents = v.substring(3).replace('.', '').replace(',', '');
        var num = new NumberLong(cents / 100);
        print(num);

        db.wizzair.update({"_id": obj["_id"]},
            {
                $set: {
                    "price": num
                }
            }
        );
        obj["price"] = num;
    }
})

