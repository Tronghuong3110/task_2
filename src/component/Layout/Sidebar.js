import React from 'react';
import "../../sass/Sidebar.scss";
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import "@fontsource/montserrat";
import {
    faChartLine,
    faClockRotateLeft,
    faCube,
    faDisplay,
    faHouse
} from '@fortawesome/free-solid-svg-icons'
import { Link } from 'react-router-dom';
const Sidebar = () => {
    return (
        <div className="sidebar">
            <div className="sidebar-title d-flex justify-content-center">
                <div className='sidebar-title-text'>
                    Modules Manager
                </div>
            </div>
            <div className="sidebar-selections">
                <div className="selection d-flex" id="dashboard">
                    <Link to={"/"}>
                        <div className="selection-symbol d-flex align-items-center">
                            <FontAwesomeIcon icon={faHouse} style={{ color: "#ffffff", fontSize: "1em" }} />
                        </div>
                        <div className="selection-text d-flex align-items-center">Dashbroad</div>
                    </Link>
                </div>
                <div className="selection d-flex" id="modules">
                    <Link to={"/modules"}>
                        <div className="selection-symbol d-flex align-items-center">
                            <FontAwesomeIcon icon={faCube} style={{ color: "#ffffff", fontSize: "1em" }} />
                        </div>
                        <div className="selection-text d-flex align-items-center">Modules</div>
                    </Link>
                </div>
                <div className="selection d-flex" id="probes">
                    <Link to={"/probe"}>
                        <div className="selection-symbol d-flex align-items-center">
                            <FontAwesomeIcon icon={faDisplay} style={{ color: "#ffffff", fontSize: "1em" }} />
                        </div>
                        <div className="selection-text d-flex align-items-center">Probes</div>
                    </Link>
                </div>
                <div className="selection d-flex" id="history">
                    <Link to={"/module_history"}>
                        <div className="selection-symbol d-flex align-items-center">
                            <FontAwesomeIcon icon={faClockRotateLeft} style={{ color: "#ffffff", fontSize: "1em" }} />
                        </div>
                        <div className="selection-text d-flex align-items-center">History</div>
                    </Link>
                </div>
                <div className="selection d-flex" id="statistic">
                    <Link>
                        <div className="selection-symbol d-flex align-items-center">
                            <FontAwesomeIcon icon={faChartLine} style={{ color: "#ffffff", fontSize: "1em" }} />
                        </div>
                        <div className="selection-text d-flex align-items-center">Statistic</div>
                    </Link>
                </div>
            </div>
        </div>
    )
}
export default Sidebar;