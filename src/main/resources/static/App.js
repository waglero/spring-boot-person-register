export default {
    name: 'App',
    data() {
        return {
            persons: [],
            person: {},
            modalTitle: 'Adicionar nova pessoa',
            apiPath: '/api/v1/persons',
            genders: {
                "M": "Masculino",
                "F": "Feminino",
                "NI": "Não Identificado"
            },
            personToBeDeleted: {}
        };
    },
    methods: {
        deletePerson: function (person) {
            this.personToBeDeleted = person;
            this.openModal('deleteModal');
        },
        edit: function (index) {
            let person = this.persons[index];
            this.person = this.normalizePerson(person);
            this.openModal('personModal');
            this.person.cpf = $('.cpf').masked(this.person.cpf);
        },
        create: function () {
            $('#formPerson').form('clear');
            this.openModal('personModal');
            this.person = {};
        },
        findAll: function () {
            let _this = this;
            axios
                .get(this.apiPath )
                .then(function (response) {
                    _this.persons = response.data
                })
                .catch(error => console.log(error))
        },
        findById: function (id, fn) {
            axios
                .get(this.apiPath  + id)
                .then(response => fn(response))
                .catch(error => console.log(error))
        },

        saveAction: function () {
            if (! this.fieldsAreValid()) {
                return false;
            }

            let person = Object.assign({}, this.person);
            person.birthDate = $('input[name="birthDate"]').val();
            person.birthDate = this.transformDateToUsFormat(person.birthDate);
            person.cpf = person.cpf.replaceAll('.', '').replaceAll('-', '');
            
            let httpMethod = 'post';
            if (person.id) {
                httpMethod = 'put';
            }
            let _this = this;
            axios[httpMethod](this.apiPath + ((person.id) ? ("/" + person.id) : ""), person)
            .then(function (response) {
                _this.persons = _this.findAll()
                _this.closeModal('personModal');
            }).catch(function (error) {
                if (error.response) {
                    Object.keys(error.response.data).forEach(function (element) {
                        $('#formPerson').form('add prompt', element, error.response.data[element]);
                    })
                }
            })
        },

        fieldsAreValid: function () {
            const personForm = $('#formPerson');
            personForm.form({
                fields: {
                    name: 'empty',
                    gender: 'empty',
                    birthDate: 'empty',
                    placeOfBirth: 'empty',
                    nationality: 'empty',
                    cpf: 'empty',
                },
                prompt: {
                    empty: 'Campo obrigatório',
                },
                inline: true,
            }).form('validate form');

            if (personForm.form('is valid')) {
                return true;
            }

            return false;
        },

        deleteAction: function (id) {
            let _this = this;
            axios
                .delete(_this.apiPath + '/' + id)
                .then(function (response) {
                    _this.persons = _this.findAll();
                    _this.closeModal('deleteModal');
                })
                .catch(error => console.log(error))
        },
        openModal: function (id) {
            $('#' + id).modal('show');
        },
        closeModal: function (id) {
            $('#' + id).modal('hide');
        },
        transformDateToUsFormat: function (date) {
            date = date.split('/');
            let newDate = date[2] + '-' + date[1] + '-' + date[0];
            return newDate;
        },
        transformDateToBrFormat: function (date) {
            date = date.split('-');
            if (date.length <= 1) {
                return date[0];
            }
            return date[2] + '/' + date[1] + '/' + date[0];
        },
        normalizePerson: function (person) {
            person.birthDate = this.transformDateToBrFormat(person.birthDate);

            return person;
        }
    },
    mounted() {
        this.persons = this.findAll();
        $(document).ready(function () {
            $('.date').mask('00/00/0000');
            $('.cpf').mask('000.000.000-00');
        })
    },
}