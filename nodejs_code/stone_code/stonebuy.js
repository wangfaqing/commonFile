const Nightmare = require('nightmare')
// const nightmare = Nightmare({ show: true })
require('nightmare-iframe-manager')(Nightmare);
const co = require('co');
aa();
function aa(){
    go()
    setTimeout(()=>aa(),1000*5*60)
    
}


function go(){
    var nightmare = Nightmare({
        show: false,
        waitTimeout:15000,
        gotoTimeout: 10000,
        loadTimeout: 20000,
        executionTimeout: 10000,
      });
    co(function *(){
        yield nightmare
        .viewport(1000,1000)
        .goto('http://www.stonebuy.com/member/')
		.click('#name')
		.wait(2000)
        .type('#name', 'fdlongfeng')
        .type('#pass','fdlongfeng#1')
        .click('#login > ul > li.btn > input[type="submit"]')
        .wait(2000)
        .click('#head > div.headNav.noshop > ul > li:nth-child(3) > a')
        .wait(2000)
        .click('#main > div.mainRight > div:nth-child(5) > ul > li:nth-child(2) > a')
        .wait(2000)
        .click('#formtd > ul > input[type="button"]:nth-child(4)')
        .wait(2000)
        .click('#head > div.headNav.noshop > ul > li:nth-child(4) > a')
        .wait(2000)
        .click('#main > div.mainRight > div:nth-child(5) > ul > li:nth-child(2) > a')
        .wait(2000)
        .click('#formpd > ul > input[type="button"]:nth-child(4)')
        .end()
        .then(console.log)
        .catch(error => {
            console.error('Search failed:', error)
    })
    });
}