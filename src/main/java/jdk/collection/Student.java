package jdk.collection;

public class Student {
    public String name;
    public String age;
    public String sex;
    public String stuNo;
    public String telNo;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getStuNo() {
        return stuNo;
    }

    public void setStuNo(String stuNo) {
        this.stuNo = stuNo;
    }

    public String getTelNo() {
        return telNo;
    }

    public void setTelNo(String telNo) {
        this.telNo = telNo;
    }

    public Student() {

    }

    public Student(String name, String age, String sex, String stuNo,
                   String telNo) {
        super();
        this.name = name;
        this.age = age;
        this.sex = sex;
        this.stuNo = stuNo;
        this.telNo = telNo;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((age == null) ? 0 : age.hashCode());
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((sex == null) ? 0 : sex.hashCode());
        result = prime * result + ((stuNo == null) ? 0 : stuNo.hashCode());
        result = prime * result + ((telNo == null) ? 0 : telNo.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        Student other = (Student) obj;
        if (age == null) {
            if (other.age != null) {
                return false;
            }
        } else if (!age.equals(other.age)) {
            return false;
        }
        if (name == null) {
            if (other.name != null) {
                return false;
            }
        } else if (!name.equals(other.name)) {
            return false;
        }
        if (sex == null) {
            if (other.sex != null) {
                return false;
            }
        } else if (!sex.equals(other.sex)) {
            return false;
        }
        if (stuNo == null) {
            if (other.stuNo != null) {
                return false;
            }
        } else if (!stuNo.equals(other.stuNo)) {
            return false;
        }
        if (telNo == null) {
            if (other.telNo != null) {
                return false;
            }
        } else if (!telNo.equals(other.telNo)) {
            return false;
        }
        return true;
    }


}
