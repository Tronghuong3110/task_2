import 'bootstrap/dist/css/bootstrap.css';
import "@fontsource/montserrat";
import '../../sass/Footer.scss'
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import {
    faBell
} from '@fortawesome/free-regular-svg-icons'
import {
    faCircleUser, faMagnifyingGlass
} from '@fortawesome/free-solid-svg-icons'
const Header = () => {
    return (
        <footer>
            <div className='company-tag'>
                Copyright © 2023 Newlife Tech
            </div>
        </footer>
    )
}
export default Header