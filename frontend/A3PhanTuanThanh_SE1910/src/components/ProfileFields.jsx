// Author: Phan Tuan Thanh - CE190595
import {localDate} from '../date.js';

export const emptyProfile = {
    customerFullName: '',
    telephone: '',
    emailAddress: '',
    customerBirthday: '',
};
export default function ProfileFields({value, onChange}) {
    const field = (name) => ({
        value: value[name] || '',
        onChange: (event) => onChange({...value, [name]: event.target.value}),
    });
    return (
        <div className="form-grid">
            <label>
                Full name
                <input required maxLength={50} autoComplete="name" {...field('customerFullName')} />
            </label>
            <label>
                Telephone
                <input
                    required
                    minLength={7}
                    maxLength={12}
                    pattern="[0-9+() \-]{7,12}"
                    autoComplete="tel"
                    {...field('telephone')}
                />
            </label>
            <label>
                Email address
                <input
                    required
                    type="email"
                    maxLength={50}
                    autoComplete="email"
                    {...field('emailAddress')}
                />
            </label>
            <label>
                Birthday
                <input
                    required
                    type="date"
                    max={localDate(-1)}
                    {...field('customerBirthday')}
                    onInput={(event) => onChange({...value, customerBirthday: event.currentTarget.value})}
                />
            </label>
        </div>
    );
}
