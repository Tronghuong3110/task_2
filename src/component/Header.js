import 'bootstrap/dist/css/bootstrap.css';
import '../sass/Header.scss';
import "@fontsource/montserrat";
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import {
    faBell
} from '@fortawesome/free-regular-svg-icons'
import {
    faCircleUser, faMagnifyingGlass
} from '@fortawesome/free-solid-svg-icons'
const Header = () => {
    return (
        <header>
            <div className="row header">
                <div className="col-lg-2 header-name">
                    SCREEN NAME
                </div>
                <div className="col-lg-10 header-content d-flex justify-content-end">
                    <div className="header-content-input">
                        <input type="text" placeholder='Search...' />
                        <div className='search-icon'>
                            <FontAwesomeIcon icon={faMagnifyingGlass} rotation={90} style={{ color: "#ffffff", }} />
                        </div>

                    </div>
                    <div className="header-content-icon">
                        <FontAwesomeIcon icon={faBell} style={{ color: "#ffffff", fontSize: 20, verticalAlign: "-webkit-baseline-middle" }} />
                    </div>
                    <div className="header-content-icon">
                        <FontAwesomeIcon icon={faCircleUser} style={{ color: "#ffffff", fontSize: 35 }} />
                    </div>
                </div>
            </div>
        </header>
    )
}
export default Header