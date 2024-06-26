// ignore_for_file: prefer_const_constructors

import 'dart:async';
import 'dart:io';
import 'package:flutter/material.dart';
import 'package:flutter_bloc/flutter_bloc.dart';
import 'package:image_cropper/image_cropper.dart';
import 'package:image_picker/image_picker.dart';
import 'package:research/functions/constants.dart';

import '../bloc/Results/results_bloc.dart';
import '../common/enums.dart';
import '../models/dto/resultsDTO.dart';

// ... (other code)

class TestPage extends StatefulWidget {
  const TestPage({Key? key, required this.couples}) : super(key: key);

  final bool couples;

  @override
  State<TestPage> createState() => _TestPageState();
}

class _TestPageState extends State<TestPage> {
  TestResults _selectedResult = TestResults.Negative;
  File? imageFile;

  bool hasImage = false;
  bool showError = false;

  //partner
  TestResults _selectedPartnerResult = TestResults.Negative;

  LinkToCare _selectedLinkToCare = LinkToCare.None;

  File? partnerImageFile;

  bool hasPartnerImage = false;
  bool showPartnerError = false;

  ResultsBloc resultsBloc = ResultsBloc();

  _pickImage() async {
    var pictureFile = await ImagePicker.platform
        .getImageFromSource(source: ImageSource.camera);

    if (pictureFile!.path.isNotEmpty) {
      showDialog(
          context: context,
          builder: (context) {
            return Center(
              child: CircularProgressIndicator(),
            );
          });
      var croppedImage = await ImageCropper.platform.cropImage(
        sourcePath: pictureFile.path,
        aspectRatio: CropAspectRatio(ratioX: 1, ratioY: 1),
        uiSettings: [
          AndroidUiSettings(
              toolbarColor: Colors.purple,
              toolbarWidgetColor: Colors.white,
              initAspectRatio: CropAspectRatioPreset.original,
              lockAspectRatio: false)
        ],
        compressQuality: 100,
        maxHeight: 700,
        maxWidth: 700,
        compressFormat: ImageCompressFormat.jpg,
      );
      setState(() {
        imageFile = File(croppedImage!.path);
        hasImage = true;
      });
      Navigator.pop(context);
    } else {
      Navigator.pop(context);
    }
  }

  _pickPartnerImage() async {
    var pictureFile = await ImagePicker.platform
        .getImageFromSource(source: ImageSource.camera);

    if (pictureFile!.path.isNotEmpty) {
      showDialog(
          context: context,
          builder: (context) {
            return Center(
              child: CircularProgressIndicator(),
            );
          });
      var croppedImage = await ImageCropper.platform.cropImage(
        sourcePath: pictureFile.path,
        aspectRatio: CropAspectRatio(ratioX: 1, ratioY: 1),
        uiSettings: [
          AndroidUiSettings(
              toolbarColor: Colors.purple,
              toolbarWidgetColor: Colors.white,
              initAspectRatio: CropAspectRatioPreset.original,
              lockAspectRatio: false)
        ],
        compressQuality: 100,
        maxHeight: 700,
        maxWidth: 700,
        compressFormat: ImageCompressFormat.jpg,
      );
      setState(() {
        partnerImageFile = File(croppedImage!.path);
        hasPartnerImage = true;
      });

      Navigator.pop(context);
    } else {
      Navigator.pop(context);
    }
  }

  int _secondsRemaining = 1200; // 20 minutes in seconds
  bool _isRunning = false;
  Timer? _timer;

  void _startTimer() {
    if (!_isRunning) {
      _isRunning = true;
      _timer = Timer.periodic(Duration(seconds: 1), (timer) {
        if (_secondsRemaining > 0) {
          setState(() {
            _secondsRemaining--;
          });
        } else {
          _stopTimer();
        }
      });
    } else {
      _stopTimer();
    }
  }

  @override
  void dispose() {
    _timer?.cancel();
    super.dispose();
  }

  void _stopTimer() async {
    setState(() {
      _timer?.cancel();
      _secondsRemaining = 1200;
      _isRunning = false;
    });
    AppConstants().playNotification();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        centerTitle: true,
        title: Text("Test"),
      ),
      body: BlocProvider(
        create: (context) => resultsBloc,
        child: Padding(
          padding: const EdgeInsets.all(8.0),
          child: SingleChildScrollView(
            child: Column(
              mainAxisAlignment: MainAxisAlignment.start,
              children: [
                Padding(
                  padding: const EdgeInsets.all(20.0),
                  child: GestureDetector(
                    onTap: _startTimer,
                    child: CircleAvatar(
                      backgroundColor: Colors.purple.shade300,
                      radius: MediaQuery.of(context).size.width *
                          0.25 *
                          MediaQuery.textScaleFactorOf(context),
                      child: Column(
                        mainAxisAlignment: MainAxisAlignment.spaceEvenly,
                        children: [
                          Text(
                            '${_secondsRemaining ~/ 60}:${(_secondsRemaining % 60).toString().padLeft(2, '0')}',
                            style: Theme.of(context)
                                .textTheme
                                .titleLarge!
                                .copyWith(
                                  color: Colors.white,
                                ),
                          ),
                          Text(_isRunning
                              ? "Tap to finish test!"
                              : "Tap to Start Test!")
                        ],
                      ),
                    ),
                  ),
                ),
                Column(
                  children: [
                    Center(
                      child: Text("Select your test results"),
                    ),
                    SizedBox(
                      height: 30,
                    ),
                    Row(
                      mainAxisAlignment: MainAxisAlignment.spaceBetween,
                      children: [
                        Column(
                          children: [
                            RadioMenuButton<TestResults>(
                              value: TestResults.Negative,
                              groupValue: _selectedResult,
                              onChanged: (value) {
                                setState(() {
                                  _selectedResult = value!;
                                });
                              },
                              child: Text('Negative'),
                            ),
                            RadioMenuButton<TestResults>(
                              value: TestResults.Positive,
                              groupValue: _selectedResult,
                              onChanged: (value) {
                                setState(() {
                                  _selectedResult = value!;
                                });
                              },
                              child: Text('Positive'),
                            ),
                            RadioMenuButton<TestResults>(
                              value: TestResults.Invalid,
                              groupValue: _selectedResult,
                              onChanged: (value) {
                                setState(() {
                                  _selectedResult = value!;
                                });
                              },
                              child: Text('Invalid'),
                            ),
                          ],
                        ),
                        GestureDetector(
                          onTap: _pickImage,
                          child: Column(
                            children: [
                              Padding(
                                padding: const EdgeInsets.all(8.0),
                                child: Container(
                                  height: 150,
                                  width: 150,
                                  color: Colors.grey.shade300,
                                  child: hasImage
                                      ? Image.file(imageFile!)
                                      : Center(
                                          child: Text("Add photo"),
                                        ),
                                ),
                              ),
                              showError
                                  ? Text(
                                      "Please select an image",
                                      style: TextStyle(color: Colors.red),
                                    )
                                  : SizedBox()
                            ],
                          ),
                        ),
                      ],
                    ),
                    SizedBox(
                      height: 20,
                    ),
                  ],
                ),
                !widget.couples
                    ? SizedBox()
                    : Column(
                        children: [
                          Center(
                            child: Text("Select your partner's test results"),
                          ),
                          SizedBox(
                            height: 30,
                          ),
                          Row(
                            mainAxisAlignment: MainAxisAlignment.spaceBetween,
                            children: [
                              Column(
                                children: [
                                  RadioMenuButton<TestResults>(
                                    value: TestResults.Negative,
                                    groupValue: _selectedPartnerResult,
                                    onChanged: (value) {
                                      setState(() {
                                        _selectedPartnerResult = value!;
                                      });
                                    },
                                    child: Text('Negative'),
                                  ),
                                  RadioMenuButton<TestResults>(
                                    value: TestResults.Positive,
                                    groupValue: _selectedPartnerResult,
                                    onChanged: (value) {
                                      setState(() {
                                        _selectedPartnerResult = value!;
                                      });
                                    },
                                    child: Text('Positive'),
                                  ),
                                  RadioMenuButton<TestResults>(
                                    value: TestResults.Invalid,
                                    groupValue: _selectedPartnerResult,
                                    onChanged: (value) {
                                      setState(() {
                                        _selectedPartnerResult = value!;
                                      });
                                    },
                                    child: Text('Invalid'),
                                  ),
                                ],
                              ),
                              GestureDetector(
                                onTap: _pickPartnerImage,
                                child: Column(
                                  children: [
                                    Padding(
                                      padding: const EdgeInsets.all(8.0),
                                      child: Container(
                                        height: 150,
                                        width: 150,
                                        color: Colors.grey.shade300,
                                        child: partnerImageFile != null
                                            ? Image.file(partnerImageFile!)
                                            : Center(
                                                child: Text("Add photo"),
                                              ),
                                      ),
                                    ),
                                    showPartnerError
                                        ? Text(
                                            "Please select an image",
                                            style: TextStyle(color: Colors.red),
                                          )
                                        : SizedBox()
                                  ],
                                ),
                              ),
                            ],
                          ),
                          SizedBox(
                            height: 20,
                          ),
                        ],
                      ),
                _selectedResult == TestResults.Positive ||
                        _selectedPartnerResult == TestResults.Positive
                    ? Column(
                        children: [
                          Padding(
                            padding: const EdgeInsets.all(8.0),
                            child: Center(
                              child: Text(
                                  "Which of the following link to care option would you wish to register to?"),
                            ),
                          ),
                          SizedBox(
                            height: 30,
                          ),
                          Row(
                            mainAxisAlignment: MainAxisAlignment.spaceBetween,
                            crossAxisAlignment: CrossAxisAlignment.start,
                            children: [
                              Column(
                                children: [
                                  RadioMenuButton<LinkToCare>(
                                    value: LinkToCare.None,
                                    groupValue: _selectedLinkToCare,
                                    onChanged: (value) {
                                      setState(() {
                                        _selectedLinkToCare = value!;
                                      });
                                    },
                                    child: Text('None'),
                                  ),
                                  RadioMenuButton<LinkToCare>(
                                    value: LinkToCare.Facility_A,
                                    groupValue: _selectedLinkToCare,
                                    onChanged: (value) {
                                      setState(() {
                                        _selectedLinkToCare = value!;
                                      });
                                    },
                                    child: Text('A'),
                                  ),
                                  RadioMenuButton<LinkToCare>(
                                    value: LinkToCare.Facility_B,
                                    groupValue: _selectedLinkToCare,
                                    onChanged: (value) {
                                      setState(() {
                                        _selectedLinkToCare = value!;
                                      });
                                    },
                                    child: Text('B'),
                                  ),
                                  RadioMenuButton<LinkToCare>(
                                    value: LinkToCare.Facility_C,
                                    groupValue: _selectedLinkToCare,
                                    onChanged: (value) {
                                      setState(() {
                                        _selectedLinkToCare = value!;
                                      });
                                    },
                                    child: Text('C'),
                                  ),
                                ],
                              )
                            ],
                          ),
                        ],
                      )
                    : SizedBox(),
                BlocBuilder<ResultsBloc, ResultsState>(
                  builder: (context, state) {
                    if (state.status == ResultsStatus.loading) {
                      return Center(
                        child: CircularProgressIndicator(),
                      );
                    }
                    return Column(
                      children: [
                        state.status == ResultsStatus.success
                            ? Text(
                                "Your test results were submitted successfully",
                                style: TextStyle(
                                    color: Colors.green, fontSize: 16))
                            : state.status == ResultsStatus.error
                                ? Text(
                                    "There was an error submitting your test results ",
                                    style: TextStyle(
                                        color: Colors.red, fontSize: 16),
                                  )
                                : SizedBox(),
                        state.status == ResultsStatus.success
                            ? SizedBox()
                            : Padding(
                                padding: const EdgeInsets.all(15.0),
                                child: ElevatedButton(
                                  onPressed: () {
                                    String individualTestType = "";
                                    String careOption = "";

                                    switch (_selectedResult) {
                                      case TestResults.Negative:
                                        individualTestType = "Negative";
                                        break;
                                      case TestResults.Positive:
                                        individualTestType = "Positive";
                                        break;
                                      case TestResults.Invalid:
                                        individualTestType = "Invalid";
                                        break;
                                    }

                                    switch (_selectedLinkToCare) {
                                      case LinkToCare.None:
                                        careOption = "";
                                        break;
                                      case LinkToCare.Facility_A:
                                        careOption = "A";
                                        break;
                                      case LinkToCare.Facility_B:
                                        careOption = "B";
                                        break;
                                      case LinkToCare.Facility_C:
                                        careOption = "C";
                                        break;
                                    }

                                    String partnerTestType = "";
                                    switch (_selectedPartnerResult) {
                                      case TestResults.Negative:
                                        partnerTestType = "Negative";
                                        break;
                                      case TestResults.Positive:
                                        partnerTestType = "Positive";
                                        break;
                                      case TestResults.Invalid:
                                        partnerTestType = "Invalid";
                                        break;
                                    }

                                    if (!hasImage) {
                                      setState(() {
                                        showError = true;
                                      });
                                      return;
                                    } else {
                                      setState(() {
                                        showError = false;
                                      });
                                    }

                                    if (!hasPartnerImage && widget.couples) {
                                      setState(() {
                                        showPartnerError = true;
                                      });
                                      return;
                                    } else {
                                      setState(() {
                                        showPartnerError = false;
                                      });
                                    }
                                    _stopTimer();
                                    resultsBloc.add(UploadResults(
                                        resultsDTO: ResultsDTO(
                                            context: context,
                                            careOption: careOption,
                                            results: individualTestType,
                                            partnerResults: widget.couples
                                                ? partnerTestType
                                                : "N/A",
                                            partnerResultsPhoto:
                                                partnerImageFile,
                                            resultsPhoto: imageFile)));
                                  },
                                  child: Text("Submit Results"),
                                ),
                              ),
                      ],
                    );
                  },
                )
              ],
            ),
          ),
        ),
      ),
    );
  }
}
