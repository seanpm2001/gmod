/**
 * Test to verify valid construction of default DDL
 * 
 * @author <a href="mailto:jeremy.rayner@bigfoot.com">Jeremy Rayner</a>
 * @version $Revision: 1.2 $
 */
package org.javanicus.gsql

import java.io.*
import java.sql.Types

class SqlGeneratorTest extends GroovyTestCase {
    def typeMap
    def build
    def database
    def sqlGenerator
              
    void setUp() {
        typeMap = new TypeMap()          
        build = new RelationalBuilder(typeMap)
        sqlGenerator = new SqlGenerator(typeMap,System.getProperty( "line.separator", "\n" ))
                  
        database = build.database(name:'genealogy') {
          table(name:'event') {
              column(name:'event_id', groovyName:'id', type:'integer', size:10, primaryKey:true, required:true)
              column(name:'description', type:'varchar', size:30)          
          }
          table(schema:'xxx', name:'individual') {
            column(name:'individual_id', type:'integer', size:10, required:true, primaryKey:true, autoIncrement:true)
            column(name:'surname', type:'varchar', size:15, required:true)
            column(name:'event_id', type:'integer', size:10)
            column(name:'num', type:Types.DECIMAL, size:'7,2')
            foreignKey(foreignTable:'event') {
                reference(local:'event_id',foreign:'event_id')
            }
            index(name:'surname_index') {
                indexColumn(name:'surname')
            }
          }
        }
    }
    
    void testGenerateDDL() {
        def testWriter = new StringWriter()
        sqlGenerator.writer = testWriter
        sqlGenerator.createDatabase(database,true)
        def expected = """\
drop table individual;

drop table event;

-- -----------------------------------------------------------------------
-- event
-- -----------------------------------------------------------------------

create table event (
    event_id integer (10) NOT NULL , 
    description varchar (30)  ,
    PRIMARY KEY (event_id)
);

ALTER TABLE event
    ADD CONSTRAINT event_PK
PRIMARY KEY (event_id);


-- -----------------------------------------------------------------------
-- xxx.individual
-- -----------------------------------------------------------------------

create table xxx.individual (
    individual_id integer (10) NOT NULL IDENTITY, 
    surname varchar (15) NOT NULL , 
    event_id integer (10)  , 
    num DECIMAL (7,2)  
);

"""
        def s = testWriter.toString()
        expected = expected.replaceAll("\r\n", "\n");
        s = s.replaceAll("\r\n", "\n");
        assertEquals (expected, s)
   }

}
