
Denon AVR Master Device
Tinozplace-West Hub
Denon AVR Master Device
« Drivers code

547

}

548

​

549

def close() {

550

    disconnect();

551

}

552

​

553

def connect(ip, port) {

554

    switch (connectionType) {

555

        case "Telnet":

556

        log.debug("Connection Type: ${connectionType}");

557

        telnetConnect([termChars:[13]], ip, port.toInteger(), null, null);

558

        case "HTTP":

559

        log.debug("Connection Type: ${connectionType}");

560

     }  

561

}

562

​

563

​

564

def disconnect() {

565

    telnetClose();

566

​

567

​

568

​

569

​

570

}

571

​

572

//commands

573

def executeCommand(command) {

574

    log.debug("Sending command: $command");

575

    command = command;

576

    sendHubCommand(new hubitat.device.HubAction(command, hubitat.device.Protocol.TELNET))

577

}

578

​

579

def refresh(){

580

    

581

    deviceCommands = getDeviceCommands()

582

    

583

    devName = device.getDataValue("type")

584

    

585

    //Setup the Class Methods

586

    deviceCommands."${devName}".each{ func, choices -> 

587

        if (choices instanceof Map){

588

            if (choices.status){

589

                executeCommand("${choices.prefix}?");

590

            }

591

        }

592

    }

593

    

594

}

595

def getNumbers(input){

596

    return input.findAll("[0-9][0-9]")*.toInteger()

597

}

598

​

599

def parseResponse(resp){

600

​

601

    data = device.getData();

602

    

603

    parseResponseDev(this, data.type, resp);

604

    

605

    deviceCommands = getDeviceCommands();                

606

    

607

    deviceCommands.each{ func, device_ -> 

608

        if (device_ instanceof Map){

609

            if (device_.child_device){

610

                def childNetworkID = "AVR_${func}_CHILD";

611

                

612

                child = getChildDevice(childNetworkID);

613

               

614

                if (child) parseResponseDev(child, func, resp);

615

            }    

616

        }

617

    }

618

}

619

​

620

def parseResponseDev(dev, name, resp){

621

    

622

    deviceCommands = getDeviceCommands();

623

     

624

    deviceCommands."$name".each{ func, choices -> 

625

        

626

        if (choices instanceof Map) {

627

            if (resp.matches("${choices.prefix}(.*)")){

628

                suffix = resp - choices.prefix;

629

                choices.commands.each{cmd, data->

630

                    num = getNumbers(suffix);   

631

                    if (num!=[] && data.val){

632

                        num = num[0];

633

                        if (num<10) suffix = suffix-("0"+num.toString());

634

                        else suffix = suffix-num.toString();

635

​

636

                        if (suffix.length() > 0 && suffix[suffix.length()-1]==" "){

637

                            suffix = suffix.substring(0, suffix.length()-1);    

638

                        }

639

                    } else {

640

                        num = null;    

641

                    }

642

                    if (suffix.matches("${data.command}")){

643

                        if (num != null) {

644

                            

645

                            if (choices.capability && data.capability && choices.capability && data.capability && choices.capability_var)

646

                                dev.sendEvent(name: choices.capability_var, value: num);

647

                            else

648

                                dev.sendEvent(name: "${func}", value: num, description: "${data.name} recieved from AVR");

649

                       

650

                        } else {

651

                            

652

                            if (choices.capability && data.capability && choices.capability && data.capability && choices.capability_var)

653

                                dev.sendEvent(name: choices.capability_var, value: data.capability_value);

654

                            else {

655

                                if (settings["${func}_${cmd}_override"] != null && settings["${func}_${cmd}_override"] != data.name)

656

                                    name = settings["${func}_${cmd}_override"]+" (${data.name})";

657

                                else 

658

                                    name = data.name;

659

                                

660

                                dev.sendEvent(name: "${func}", value: name, description: "${data.name}::${name} recieved from AVR");

661

               

662

                            }

663

                        }

664

​

665

                    }

666

​

667

                }

668

            }

669

        }

670

    }

671

}

672

​

673

​

674

def parse(command) {

675

    log.debug("Got Response: ${command}");

676

    commandReceived = true;

677

    parseResponse(command);

678

    

679

    return null;

680

}

681

​

682

def refreshTimeout(){

683

    log.debug("commandReceived = ${commandReceived}");

684

}

685

​

686

​

