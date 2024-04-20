import { useCallback, useEffect } from 'react';
import { SubmitHandler, useForm } from 'react-hook-form';
import { MultiValue } from 'react-select';
import { useFetch } from 'use-http';
import FormErrorWrapper from '../../../components/form-error-wrapper/FormErrorWrapper';
import FormInput from '../../../components/form-input/FormInput';
import { authPaths } from '../../../config/api';
import useValidators from '../../../hooks/useValidator';
import './Register.scss';
import RegisterSkillsSelect, {
  SkillOption,
} from './register-skills-select/RegisterSkillsSelect';

type Inputs = {
  'First Name': string;
  'Last Name': string;
  'Current Workplace': string;
  Education: string;
  Experience: string;
  Address: string;
  Email: string;
  Password: string;
  'Repeat Password': string;
  WICHW: string;
  skillsHave: MultiValue<SkillOption>;
  skillsNeed: MultiValue<SkillOption>;
};

function Register() {
  const { auth } = useValidators();

  const {
    handleSubmit,
    control,
    register,
    reset,
    watch,
    setError,
    clearErrors,
    setValue,
    formState: { errors },
  } = useForm<Inputs>({
    defaultValues: {
      'First Name': '',
      'Last Name': '',
      'Current Workplace': '',
      Education: '',
      Experience: '',
      Address: '',
      Email: '',
      Password: '',
      'Repeat Password': '',
      WICHW: '',
      skillsHave: [],
      skillsNeed: [],
    },
    mode: 'onChange',
  });

  const formValues = watch();
  const { post, response, loading } = useFetch<object>(authPaths.register);

  useEffect(() => {
    const areEqual = formValues.Password === formValues['Repeat Password'];
    const hasError = Boolean(errors['Repeat Password']);
    const hasManualError =
      hasError && errors['Repeat Password']?.type === 'manual';

    if (!hasError && !areEqual) {
      setError('Repeat Password', {
        type: 'manual',
        message: 'Repeat password must match password',
      });
    }

    if (hasManualError && areEqual) {
      clearErrors('Repeat Password');
    }
  }, [errors, formValues, setError, clearErrors]);

  const onSubmit: SubmitHandler<Inputs> = async (data) => {
    console.log(data);
    await post({
      firstName: data['First Name'].trim(),
      lastName: data['Last Name'].trim(),
      workplace: data['Current Workplace'].trim(),
      education: data.Education,
      experience: data.Experience,
      address: data.Address,
      email: data.Email,
      password: data.Password,
      skillsHave: data.skillsHave.map((x) => Number(x.value)),
      skillsNeed: data.skillsNeed.map((x) => Number(x.value)),
    });
  };

  const mockedSkills = [...new Array(5)].map((x, i) => ({
    value: i.toString(),
    label: 'Software Engineering' + i * 32,
  }));

  const onSkillsHaveChange = useCallback(
    (val: MultiValue<SkillOption>) => {
      setValue('skillsHave', val, {
        shouldValidate: true,
        shouldDirty: true,
        shouldTouch: true,
      });
    },
    [setValue]
  );

  const onSkillsNeedChange = useCallback(
    (val: MultiValue<SkillOption>) => {
      setValue('skillsNeed', val, {
        shouldValidate: true,
        shouldDirty: true,
        shouldTouch: true,
      });
    },
    [setValue]
  );

  return (
    <div className="row">
      <div className="col-sm-9 col-md-7 col-lg-5 mx-auto">
        <div className="card border-0 shadow rounded-3 my-5">
          <div className="card-body p-4 p-sm-5">
            <h5 className="card-title text-center mb-5 fw-light fs-5">
              Sign In
            </h5>
            <form onSubmit={handleSubmit(onSubmit)}>
              <FormInput
                control={control}
                type="email"
                name="Email"
                inputClasses="form-control"
                placeholder="name@example.com"
                labelText="Email Address*"
                rules={auth.EMAIL_VALIDATIONS}
              />

              <FormInput
                control={control}
                type="text"
                inputClasses="form-control"
                name="First Name"
                placeholder="John"
                labelText="First Name*"
                rules={auth.FIRST_NAME_VALIDATIONS}
              />

              <FormInput
                control={control}
                type="text"
                inputClasses="form-control"
                name="Last Name"
                placeholder="Johnson"
                labelText="Last Name*"
                rules={auth.LAST_NAME_VALIDATIONS}
              />

              <FormInput
                control={control}
                type="text"
                inputClasses="form-control"
                name="Current Workplace"
                placeholder="LudogorieSoft, Targovishte 7700, Bulgaria"
                labelText="Workplace"
              />

              <FormInput
                control={control}
                type="text"
                inputClasses="form-control"
                name="Education"
                placeholder="1 SU 'st. Sedmochislenici'"
                labelText="Education*"
                rules={auth.EDUCATION_VALIDATIONS}
              />

              <FormInput
                control={control}
                type="password"
                inputClasses="form-control"
                name="Password"
                placeholder="Password"
                labelText="Password*"
                rules={auth.PASSWORD_VALIDATIONS}
              />

              <FormInput
                control={control}
                type="password"
                inputClasses="form-control"
                name="Repeat Password"
                placeholder="Repeat Password"
                labelText="Repeat Password*"
                rules={auth.REPEAT_PASSWORD_VALIDATIONS}
              />

              <FormErrorWrapper message={errors.Experience?.message}>
                <div className="form-floating">
                  <textarea
                    className="form-control"
                    placeholder="Leave a comment here"
                    id="floatingTextarea2"
                    style={{ height: '100px' }}
                    {...register('Experience', {
                      ...auth.EXPERIENCE_VALIDATIONS,
                    })}></textarea>
                  <label htmlFor="floatingTextarea2">Experience*</label>
                </div>
              </FormErrorWrapper>

              <FormErrorWrapper message={errors.WICHW?.message}>
                <div className="form-floating">
                  <textarea
                    className="form-control"
                    placeholder="Leave a comment here"
                    id="floatingTextarea2"
                    style={{ height: '100px' }}
                    {...register('WICHW', {
                      ...auth.WICHW_VALIDATIONS,
                    })}></textarea>
                  <label htmlFor="floatingTextarea2">
                    What I can help with*
                  </label>
                </div>
              </FormErrorWrapper>

              <FormErrorWrapper message={undefined}>
                <RegisterSkillsSelect
                  options={mockedSkills}
                  placeholder={'Select what skills you HAVE...'}
                  onChange={onSkillsHaveChange}
                />
              </FormErrorWrapper>
              <FormErrorWrapper message={undefined}>
                <RegisterSkillsSelect
                  options={mockedSkills}
                  placeholder={'Select what skills you NEED...'}
                  onChange={onSkillsNeedChange}
                />
              </FormErrorWrapper>

              <div className="d-grid">
                <button
                  className="btn btn-primary btn-login text-uppercase fw-bold"
                  type="submit">
                  Sign in
                </button>
              </div>
              <hr className="my-4" />
              <div className="d-grid mb-2">
                <button
                  className="btn btn-google btn-login text-uppercase fw-bold"
                  type="submit">
                  <i className="fab fa-google me-2" /> Sign in with Google
                </button>
              </div>
              <div className="d-grid">
                <button
                  className="btn btn-facebook btn-login text-uppercase fw-bold"
                  type="submit">
                  <i className="fab fa-facebook-f me-2" /> Sign in with Facebook
                </button>
              </div>
            </form>
          </div>
        </div>
      </div>
    </div>
  );
}

export default Register;