const Nightmare = require('nightmare')
const nightmare = Nightmare({ show: true })
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
nightmare
    .viewport(1000,1000)
    .goto('http://my.stonesm.com/join/login.aspx')
    .type('#UID', 'longfeng')
    .type('#PWD','5566')
    .click('#Login_btn')
    .wait(2000)
    .click('#form1 > div.wrap > div > div.fl.vip_left.vip_magLeft > dl:nth-child(2) > dd > p:nth-child(2) > a')
    .wait(2000)
    .enterIFrame('#form1 > div.wrap > div > div.fr.vip_right.vip_magRight > div > iframe')
    .wait(2000)
    .click('#Data1_ctl01_chkHeader')
    .wait(2000)
    .click('#LinkButton1')
    .wait(2000)
/** 第2页 */
    .click('#Pager1 > a:nth-child(3)')
    .wait(2000)
    .click('#Data1_ctl01_chkHeader')
    .wait(2000)
    .click('#LinkButton1')
    .wait(2000)
/** 第3页 */
    .click('#Pager1 > a:nth-child(3)')
    .wait(2000)
    .click('#Data1_ctl01_chkHeader')
    .wait(2000)
    .click('#LinkButton1')
/** 第4页 */   
    .wait(2000)
    .click('#Pager1 > a:nth-child(3)')
    .wait(2000)
    .click('#Data1_ctl01_chkHeader')
    .wait(2000)
    .click('#LinkButton1')
/** 第5页 */
    .wait(2000)
    .click('#Pager1 > a:nth-child(3)')
    .wait(2000)
    .click('#Data1_ctl01_chkHeader')
    .wait(2000)
    .click('#LinkButton1')
/** 第6页 */
    .wait(2000)
    .click('#Pager1 > a:nth-child(3)')
    .wait(2000)
    .click('#Data1_ctl01_chkHeader')
    .wait(2000)
    .click('#LinkButton1')

    .end()
    .then(console.log)
    .catch(error => {
        console.error('Search failed:', error)
  })
});
}