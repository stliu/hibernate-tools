/*
* Hibernate, Relational Persistence for Idiomatic Java
*
* Copyright (c) 2011, Red Hat Inc. or third-party contributors as
* indicated by the @author tags or express copyright attribution
* statements applied by the authors.  All third-party contributions are
* distributed under license by Red Hat Inc.
*
* This copyrighted material is made available to anyone wishing to use, modify,
* copy, or redistribute it subject to the terms and conditions of the GNU
* Lesser General Public License, as published by the Free Software Foundation.
*
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
* or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
* for more details.
*
* You should have received a copy of the GNU Lesser General Public License
* along with this distribution; if not, write to:
* Free Software Foundation, Inc.
* 51 Franklin Street, Fifth Floor
* Boston, MA  02110-1301  USA
*/

public class ConstructorUsage {

    public void personConstructor() {
        Person defaultConstructor = new Person();
        Person minimalConstructor = new Person( "a name", new EntityAddress( "street", "city" ) );
    }

    public void employeeConstructor() {
        Employee defaultConstructor = new Employee();
        Employee fullConstructor = new Employee( "a name", new EntityAddress( "street", "city" ), 2.0 );
        Employee minimalConstructor = new Employee( "a name", new EntityAddress( "street", "city" ) );
    }

    public void entityAddressConstructor() {
        EntityAddress defaultConstructor = new EntityAddress();
        EntityAddress allConstructor = new EntityAddress( "street", "city" );
        EntityAddress minimalConstructor = new EntityAddress( "street" );
    }

    public void companyConstructor() {
        Company defaultConstructor = new Company();
        CompanyId cid = new CompanyId( 42, 'a' );
        Company allConstructor = new Company( cid, "myBrand", new java.util.HashSet() );
        Company minimalConstructor = new Company( cid, "myBrand" );
    }

    public void productConstructor() {
        BrandProduct defaultConstructor = new BrandProduct();
        BrandProduct minimalConstructor = new BrandProduct( "id" );
        BrandProduct fullConstructor = new BrandProduct( "id", "a name" );
    }
}
